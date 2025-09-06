package com.niranjan.ems.controller;

import com.niranjan.ems.models.User;
import com.niranjan.ems.repo.UserRepository;
import com.niranjan.ems.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping("/managers")
    public List<User> getManagers() {
        return service.getManagers();
    }
}
