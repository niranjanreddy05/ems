package com.niranjan.ems.controller;

import com.niranjan.ems.models.Department;
import com.niranjan.ems.models.User;
import com.niranjan.ems.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return service.getAllDepartments();
    }

    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable Long id) {
        return service.getDepartmentById(id);
    }

    @PatchMapping("/{deptId}/assign-employee/{userId}")
    public ResponseEntity<?> assignEmployee(@PathVariable Long deptId, @PathVariable Long userId) {
        try {
            User updated = service.assignEmployeeToDepartment(deptId, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/unassign-employee/{userId}")
    public ResponseEntity<?> unassignEmployee(@PathVariable Long userId) {
        try {
            User updated = service.unassignEmployeeFromDepartment(userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
