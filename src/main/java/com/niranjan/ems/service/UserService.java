package com.niranjan.ems.service;

import com.niranjan.ems.models.Department;
import com.niranjan.ems.models.Project;
import com.niranjan.ems.models.User;
import com.niranjan.ems.models.UserPrincipal;
import com.niranjan.ems.repo.DepartmentRepository;
import com.niranjan.ems.repo.ProjectRepository;
import com.niranjan.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private ProjectRepository projectRepo;


    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return user;
    }

    public String verify(User user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            return jwtService.generateToken(userDetails.getUsername(), role);
        } else {
            return "fail";
        }
    }

    public List<User> getAllUsers(UserPrincipal userPrincipal) {
        String role = userPrincipal.getAuthorities().iterator().next().getAuthority();

        if ("ADMIN".equals(role)) {
            // ADMIN → return all users
            return repo.findAll();
        }

        if ("MANAGER".equals(role)) {
            // MANAGER → only employees from their department
            Long deptId = userPrincipal.getUser().getDepartment().getId();
            return repo.findByRoleAndDepartmentId("EMPLOYEE", deptId);
        }

        // EMPLOYEE (or anyone else) → not allowed
        throw new AccessDeniedException("Employees are not allowed to view user list");
    }

    public User getEmployeeByIdWithCheck(Long id, UserPrincipal userPrincipal) {
        // Find the requested user (employee) by id
        User requestedUser = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the current user's role
        String role = userPrincipal.getAuthorities().iterator().next().getAuthority();

        // ADMIN: can access anyone
        if (role.equals("ADMIN")) {
            return requestedUser;
        }

        // MANAGER: can access employees in their department only
        if (role.equals("MANAGER")) {
            if(requestedUser.getId().equals(userPrincipal.getUser().getId())){
                return requestedUser;
            }
            else if (requestedUser.getDepartment() != null &&
                    requestedUser.getDepartment().getId().equals(userPrincipal.getUser().getDepartment().getId()) &&
                    requestedUser.getRole().equals("EMPLOYEE")) {
                return requestedUser;
            } else {
                throw new AccessDeniedException("Managers can only access employees from their own department");
            }
        }

        // EMPLOYEE: can only access themselves
        if (role.equals("EMPLOYEE")) {
            if (requestedUser.getId().equals(userPrincipal.getUser().getId())) {
                return requestedUser;
            } else {
                throw new AccessDeniedException("Employees can only access their own data");
            }
        }

        throw new AccessDeniedException("Not allowed");
    }

    public List<User> getManagers() {
        return repo.findByRole("MANAGER");
    }

    public User promoteUser(Long id) {
        User tempUser = repo.findById(id).orElse(null);

        if (tempUser == null) {
            throw new RuntimeException("User with id " + id + " not found!");
        }

        if ("MANAGER".equalsIgnoreCase(tempUser.getRole()) || "ADMIN".equalsIgnoreCase(tempUser.getRole())) {
            throw new RuntimeException("User " + tempUser.getName() + " is already " + tempUser.getRole() + " and cannot be promoted.");
        }

        tempUser.setRole("MANAGER");
        return repo.save(tempUser);
    }

    public User demoteUser(Long id) {
        User tempUser = repo.findById(id).orElse(null);

        if (tempUser == null) {
            throw new RuntimeException("User with id " + id + " not found!");
        }

        if (!"MANAGER".equals(tempUser.getRole())) {
            throw new RuntimeException("User with id " + id + " is not a manager!");
        }

        Department dept = tempUser.getDepartment();

        //  Check if this is the only manager in the department
        long managerCount = repo.countByDepartmentAndRole(dept, "MANAGER");
        if (managerCount <= 1) {
            throw new RuntimeException(
                    "Cannot demote. Department " + dept.getName() + " must have at least 1 manager!"
            );
        }

        // Check if this manager is assigned to any projects
        List<Project> assignedProjects = projectRepo.findByManager(tempUser);
        if (!assignedProjects.isEmpty()) {
            throw new RuntimeException(
                    "Cannot demote. User with id " + id + " is still managing "
                            + assignedProjects.size() + " project(s)!"
            );
        }

        // Passed all checks -> demote to employee
        tempUser.setRole("EMPLOYEE");
        return repo.save(tempUser);
    }

}
