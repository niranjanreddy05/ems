package com.niranjan.ems.service;


import com.niranjan.ems.models.Department;
import com.niranjan.ems.repo.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository repo;

    public List<Department> getAllDepartments() {
        return repo.findAll();
    }

    public Department getDepartmentById(Long id) {
        return repo.findById(id).orElse(null);
    }

}
