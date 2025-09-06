package com.niranjan.ems.service;


import com.niranjan.ems.models.Project;
import com.niranjan.ems.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repo;

    public List<Project> getAllProjects() {
        return repo.findAll();
    }

    public Project getProjectById(Long id) {
        return repo.findById(id).orElse(null);
    }

}
