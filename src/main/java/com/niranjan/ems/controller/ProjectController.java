package com.niranjan.ems.controller;

import com.niranjan.ems.models.Project;
import com.niranjan.ems.models.UserPrincipal;
import com.niranjan.ems.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return service.getAllProjects();
    }

    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return service.getProjectById(id);
    }

    @PatchMapping("/{projectId}/assign-manager/{userId}")
    public ResponseEntity<?> assignManager(@PathVariable Long projectId, @PathVariable Long userId) {
        try {
            Project updated = service.assignManager(projectId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{projectId}/assign-employee/{userId}")
    public ResponseEntity<?> assignEmployee(@PathVariable Long projectId, @PathVariable Long userId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Project updated = service.assignEmployee(projectId, userId, userPrincipal);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{projectId}/unassign-manager")
    public ResponseEntity<?> unassignManager(@PathVariable Long projectId) {
        try {
            Project updated = service.unassignManagerFromProject(projectId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Unassign employee
    @PatchMapping("/{projectId}/unassign-employee/{userId}")
    public ResponseEntity<?> unassignEmployee(@PathVariable Long projectId, @PathVariable Long userId,  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Project updated = service.unassignEmployeeFromProject(projectId, userId, userPrincipal);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
