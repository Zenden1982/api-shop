package com.teamwork.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.Role;
import com.teamwork.api.repository.RoleRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Roles", description = "Управление ролями.")
public class RoleController {

    private final RoleRepository roleRepository;

    @Operation(summary = "Получить все роли", description = "Возвращает список всех ролей в системе. Доступно только администраторам.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @Operation(summary = "Удалить роль", description = "Удаляет роль по ID. Будьте осторожны, это может повлиять на доступ пользователей. Доступно только администраторам.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable @Positive Long id) {
        // Дополнительная защита: не даем удалить базовые роли
        Role roleToDelete = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Роль с ID " + id + " не найдена"));

        if ("ROLE_ADMIN".equals(roleToDelete.getName()) || "ROLE_USER".equals(roleToDelete.getName())) {
            throw new UnsupportedOperationException("Нельзя удалить базовые системные роли.");
        }

        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
