package com.niranjan.ems.controller;

import com.niranjan.ems.models.Project;
import com.niranjan.ems.repo.ProjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepo;

    public ProjectController(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectRepo.findById(id).orElse(null);
    }
}
