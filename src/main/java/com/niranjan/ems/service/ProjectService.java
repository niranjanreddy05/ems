package com.niranjan.ems.service;


import com.niranjan.ems.models.Project;
import com.niranjan.ems.models.User;
import com.niranjan.ems.models.UserPrincipal;
import com.niranjan.ems.repo.ProjectRepository;
import com.niranjan.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepo.findById(id).orElse(null);
    }

    @Transactional
    public Project assignManager(Long projectId, Long userId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"MANAGER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("User is not a manager");
        }

        if (project.getManager() != null) {
            throw new RuntimeException("Project already has a manager");
        }

        project.setManager(user);
        return projectRepo.save(project);
    }

    @Transactional
    public Project assignEmployee(Long projectId, Long userId, UserPrincipal requester) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // only employees can be assigned
        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("User is not an employee");
        }

        String requesterRole = requester.getAuthorities().iterator().next().getAuthority();

        // if requester is MANAGER -> check project ownership
        if ("MANAGER".equalsIgnoreCase(requesterRole)) {
            if (!project.getManager().getId().equals(requester.getUser().getId())) {
                throw new RuntimeException("Managers can only assign employees to their own projects");
            }
        }

        // if requester is EMPLOYEE -> block immediately
        if ("EMPLOYEE".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("Employees cannot assign projects");
        }

        // assign employee if not already assigned
        if (project.getEmployees().contains(user)) {
            throw new RuntimeException("Employee is already assigned with this project");
        }

        project.getEmployees().add(user);
        return projectRepo.save(project);
    }

    // Unassign manager from project
    @Transactional
    public Project unassignManagerFromProject(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getManager() == null) {
            throw new RuntimeException("Project has no manager assigned");
        }

        project.setManager(null);
        return projectRepo.save(project);
    }

    //  Unassign employee from project
    @Transactional
    public Project unassignEmployeeFromProject(Long projectId, Long userId, UserPrincipal requester) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("User is not an employee");
        }

        if (!project.getEmployees().contains(user)) {
            throw new RuntimeException("User is not assigned to this project");
        }

        String requesterRole = requester.getAuthorities().iterator().next().getAuthority();

        // if requester is MANAGER → check project ownership
        if ("MANAGER".equalsIgnoreCase(requesterRole)) {
            if (!project.getManager().getId().equals(requester.getUser().getId())) {
                throw new RuntimeException("Managers can only unassign employees from their own projects");
            }
        }

        // if requester is EMPLOYEE → block immediately
        if ("EMPLOYEE".equalsIgnoreCase(requesterRole)) {
            throw new RuntimeException("Employees cannot unassign from projects");
        }

        // Perform unassignment
        project.getEmployees().remove(user);
        user.getProjects().remove(project); // update both sides

        projectRepo.save(project);
        userRepo.save(user);

        return project;
    }

}
