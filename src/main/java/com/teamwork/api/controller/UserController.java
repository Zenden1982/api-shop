package com.teamwork.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.User;
import com.teamwork.api.service.UserService;

import lombok.Data;

@RestController
@Data
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers(0, 10));
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        // Implement user creation logic here
        return ResponseEntity.ok(userService.addUser(user));
    }
}
