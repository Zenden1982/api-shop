package com.teamwork.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.AuthRequest;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.UserCreateUpdateDTO;
import com.teamwork.api.model.DTO.UserReadDTO;
import com.teamwork.api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<UserReadDTO> createUser(@RequestBody UserCreateUpdateDTO userDTO) {
        UserReadDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<UserReadDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getAllUsers(page, size);
        Page<UserReadDTO> userReadDTOPage = userPage.map(UserReadDTO::toUserReadDTO);
        return ResponseEntity.ok(userReadDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserReadDTO> getUserById(@PathVariable Long id) {
        UserReadDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserReadDTO> updateUser(@PathVariable Long id, @RequestBody UserCreateUpdateDTO userDTO) {
        UserReadDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest user) {
        return ResponseEntity.status(200).body(userService.generateToken(user, authenticationManager));
    }
}
