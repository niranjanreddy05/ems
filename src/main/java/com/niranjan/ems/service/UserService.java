package com.niranjan.ems.service;

import com.niranjan.ems.models.User;
import com.niranjan.ems.repo.DepartmentRepository;
import com.niranjan.ems.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<User> getManagers() {
        return repo.findByRole("MANAGER");
    }

}
