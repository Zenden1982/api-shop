package com.teamwork.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.teamwork.api.repository.UserRepository;
import com.teamwork.api.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Orders", description = "Создание и управление заказами")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<OrderReadDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(403).build();
        }
        Page<OrderReadDTO> orders = orderService.findAll(page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderReadDTO> getById(@PathVariable Long id) {
        var opt = orderService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        OrderReadDTO dto = opt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // allow if admin or order owner
        if (isAdmin || (dto.getUser() != null && currentUsername.equals(dto.getUser().getUsername()))) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrderReadDTO> getByUserId(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(orderService.findByUserId(userId));
        }

        // if not admin, allow only if the requested userId equals current user's id
        var currentUserOpt = userRepository.findByUsername(currentUsername);
        if (currentUserOpt.isPresent() && currentUserOpt.get().getId().equals(userId)) {
            return ResponseEntity.ok(orderService.findByUserId(userId));
        }
        return ResponseEntity.status(403).build();
    }

    @Operation(summary = "Создать заказ", description = "Создаёт заказ. Пример тела запроса:\n{\n  \"userId\": 1,\n  \"items\": [{ \"productId\": 10, \"quantity\": 2 }],\n  \"shippingAddress\": \"ул. Ленина, 1\",\n  \"phoneNumber\": \"+79991234567\"\n}")
    @PostMapping
    public ResponseEntity<OrderReadDTO> create(@RequestBody OrderCreateUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // if not admin, user can create only orders for themselves
        if (!isAdmin) {
            var currentUserOpt = userRepository.findByUsername(currentUsername);
            if (currentUserOpt.isEmpty() || !currentUserOpt.get().getId().equals(dto.getUserId())) {
                return ResponseEntity.status(403).build();
            }
        }

        OrderReadDTO created = orderService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var opt = orderService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        OrderReadDTO dto = opt.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !(dto.getUser() != null && currentUsername.equals(dto.getUser().getUsername()))) {
            return ResponseEntity.status(403).build();
        }

        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderReadDTO> update(@PathVariable Long id, @RequestBody OrderCreateUpdateDTO dto) {
        var opt = orderService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        OrderReadDTO existing = opt.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !(existing.getUser() != null && currentUsername.equals(existing.getUser().getUsername()))) {
            return ResponseEntity.status(403).build();
        }

        OrderReadDTO updated = orderService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

}
