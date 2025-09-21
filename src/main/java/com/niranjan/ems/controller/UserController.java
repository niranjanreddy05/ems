package com.niranjan.ems.controller;

import com.niranjan.ems.models.User;
import com.niranjan.ems.models.UserPrincipal;
import com.niranjan.ems.repo.UserRepository;
import com.niranjan.ems.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<User> getAllUsers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return service.getAllUsers(userPrincipal);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = service.getEmployeeByIdWithCheck(id, userPrincipal);
        return user;
    }

    @GetMapping("/managers")
    public List<User> getManagers() {
        return service.getManagers();
    }

    @PatchMapping("/{id}/promote")
    public ResponseEntity<?> promoteUser(@PathVariable Long id) {
        try {
            User updatedUser = service.promoteUser(id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/demote")
    public ResponseEntity<?> demoteUser(@PathVariable Long id) {
        try {
            User updatedUser = service.demoteUser(id);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/register")
    public User register(@RequestBody User user) {

        user.setRole("EMPLOYEE");
        return service.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        System.out.println(user);
        return service.verify(user);
    }


}
