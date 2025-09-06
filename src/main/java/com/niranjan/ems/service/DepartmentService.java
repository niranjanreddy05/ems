package com.niranjan.ems.service;


import com.niranjan.ems.models.Department;
import com.niranjan.ems.models.User;
import com.niranjan.ems.repo.DepartmentRepository;
import com.niranjan.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepo;

    @Autowired
    private UserRepository userRepo;

    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepo.findById(id).orElse(null);
    }

    @Transactional
    public User assignEmployeeToDepartment(Long deptId, Long userId) {
        Department dept = departmentRepo.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("User is not an employee");
        }

        if (user.getDepartment() != null) {
            throw new RuntimeException("User already belongs to a department");
        }

        user.setDepartment(dept);
        return userRepo.save(user);
    }

    @Transactional
    public User unassignEmployeeFromDepartment(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("User is not an employee");
        }

        if (user.getDepartment() == null) {
            throw new RuntimeException("User is not assigned to any department");
        }

        user.setDepartment(null);
        return userRepo.save(user);
    }

}
