package com.teamwork.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.AuthRequest;
import com.teamwork.api.model.DTO.UserCreateUpdateDTO;
import com.teamwork.api.model.DTO.UserReadDTO;
import com.teamwork.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Регистрация, аутентификация и управление пользователями")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    // --- ПУБЛИЧНЫЕ ЭНДПОИНТЫ ---

    @Operation(summary = "Регистрация нового пользователя", description = "Этот эндпоинт открыт для всех. Создает нового пользователя и корзину для него.")
    @PostMapping("/register") // Рекомендуется сменить путь на /register для ясности
    public ResponseEntity<UserReadDTO> createUser(@RequestBody @Valid UserCreateUpdateDTO userDTO) {
        UserReadDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Аутентификация (вход и получение JWT)", description = "Этот эндпоинт открыт для всех. Принимает логин и пароль, возвращает JWT.")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(userService.generateToken(authRequest, authenticationManager));
    }

    // --- ЭНДПОИНТЫ ДЛЯ АДМИНИСТРАТОРОВ ---

    @Operation(summary = "Получить список всех пользователей", description = "Доступно только администраторам.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Page<UserReadDTO>> getAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        Page<UserReadDTO> userPage = userService.getAllUsers(pageable).map(UserReadDTO::toUserReadDTO);
        return ResponseEntity.ok(userPage);
    }

    @Operation(summary = "Удалить пользователя", description = "Полностью удаляет пользователя и все связанные с ним данные. Доступно только администраторам.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Назначить роли пользователю", description = "Присваивает или изменяет роли пользователя. Доступно только администраторам.")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> assignRolesToUser(@PathVariable @Positive Long id,
            @RequestBody List<String> roleNames) {
        userService.assignRolesToUser(id, roleNames);
        return ResponseEntity.ok().build();
    }

    // --- ОБЩИЕ ЗАЩИЩЕННЫЕ ЭНДПОИНТЫ (Личный кабинет и администрирование) ---

    @Operation(summary = "Получить информацию о пользователе по ID", description = "Пользователь может получить информацию только о себе. Администратор — о любом пользователе.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == @userService.findByUsername(authentication.name).id")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<UserReadDTO> getUserById(@PathVariable @Positive Long id) {
        UserReadDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Обновить информацию о пользователе", description = "Пользователь может обновить только свой профиль. Администратор — профиль любого пользователя.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == @userService.findByUsername(authentication.name).id")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<UserReadDTO> updateUser(@PathVariable @Positive Long id,
            @RequestBody @Valid UserCreateUpdateDTO userDTO) {
        UserReadDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
