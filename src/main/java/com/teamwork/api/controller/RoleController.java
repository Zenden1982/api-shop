package com.teamwork.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.teamwork.api.model.Role;
import com.teamwork.api.repository.RoleRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/roles")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(Long id) {
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
