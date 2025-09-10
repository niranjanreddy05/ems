package com.niranjan.ems.service;

import com.niranjan.ems.models.Department;
import com.niranjan.ems.models.Project;
import com.niranjan.ems.models.User;
import com.niranjan.ems.repo.DepartmentRepository;
import com.niranjan.ems.repo.ProjectRepository;
import com.niranjan.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

            // Assuming your UserDetails stores role in authorities
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            return jwtService.generateToken(userDetails.getUsername(), role);
        } else {
            return "fail";
        }
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
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
