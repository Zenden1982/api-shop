package com.teamwork.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.DTO.OrderCreateUpdateDTO;
import com.teamwork.api.model.DTO.OrderReadDTO;
import com.teamwork.api.model.DTO.OrderUpdateAdminDTO;
import com.teamwork.api.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Orders", description = "Управление заказами")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все заказы (только для админа)")
    public ResponseEntity<Page<OrderReadDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderReadDTO> orders = orderService.findAll(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.findById(#id).get().user.username == authentication.name")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<OrderReadDTO> getById(@PathVariable @Positive Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Получить все заказы текущего пользователя")
    public ResponseEntity<List<OrderReadDTO>> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        // Сервис должен найти пользователя по имени и затем его заказы
        List<OrderReadDTO> userOrders = orderService.findAllByUsername(currentUsername);
        return ResponseEntity.ok(userOrders);
    }

    @PostMapping("/from-cart")
    @Operation(summary = "Создать заказ из корзины", description = "Создает заказ на основе текущего содержимого корзины пользователя. В теле запроса нужно передать только адрес и телефон.")
    public ResponseEntity<OrderReadDTO> createOrderFromCart(@Valid @RequestBody OrderCreateUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        OrderReadDTO createdOrder = orderService.createFromCart(currentUsername, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить заказ (только для админа)", description = "Позволяет администратору изменить статус, адрес или телефон заказа.")
    public ResponseEntity<OrderReadDTO> update(@PathVariable @Positive Long id,
            @Valid @RequestBody OrderUpdateAdminDTO dto) {
        OrderReadDTO updated = orderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить заказ (только для админа)")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
