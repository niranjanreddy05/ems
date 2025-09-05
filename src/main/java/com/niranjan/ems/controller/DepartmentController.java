package com.niranjan.ems.controller;

import com.niranjan.ems.models.Department;
import com.niranjan.ems.repo.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepo;

    public DepartmentController(DepartmentRepository departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }

    @GetMapping("/{id}")
    public Department getDepartmentById(@PathVariable Long id) {
        return departmentRepo.findById(id).orElse(null);
    }
}
