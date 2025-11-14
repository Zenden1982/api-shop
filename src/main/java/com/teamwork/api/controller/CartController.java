package com.teamwork.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.DTO.AddItemToCartRequestDTO;
import com.teamwork.api.model.DTO.CartDTO;
import com.teamwork.api.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Управление корзиной пользователя")
@SecurityRequirement(name = "BearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Получить содержимое корзины", description = "Возвращает текущее состояние корзины пользователя, включая список товаров и общую стоимость.")
    public ResponseEntity<CartDTO> getCart() {
        String username = getCurrentUsername();
        CartDTO cartDTO = cartService.getCartDTO(username);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/items")
    @Operation(summary = "Добавить товар в корзину", description = "Добавляет товар в корзину или увеличивает его количество, если он уже там есть.")
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody AddItemToCartRequestDTO requestDTO) {
        String username = getCurrentUsername();
        CartDTO updatedCart = cartService.addItemToCart(username, requestDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Удалить товар из корзины", description = "Удаляет конкретный элемент (товар) из корзины по его ID.")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable @Positive(message = "ID элемента корзины должен быть положительным") Long cartItemId) {
        String username = getCurrentUsername();
        cartService.removeItemFromCart(username, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины текущего пользователя.")
    public ResponseEntity<Void> clearCart() {
        String username = getCurrentUsername();
        cartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Вспомогательный метод для получения имени текущего аутентифицированного
     * пользователя.
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // Этого не должно произойти, если SecurityConfig настроен правильно,
            // но это хорошая защитная проверка.
            throw new IllegalStateException("Нет аутентифицированного пользователя");
        }
        return authentication.getName();
    }
}
