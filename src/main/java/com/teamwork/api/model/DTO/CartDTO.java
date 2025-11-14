package com.teamwork.api.model.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.teamwork.api.model.Cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private BigDecimal grandTotal; // Общая стоимость всей корзины

    public static CartDTO fromCart(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                .map(CartItemDTO::fromCartItem)
                .collect(Collectors.toList());

        BigDecimal grandTotal = itemDTOs.stream()
                .map(CartItemDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemDTOs)
                .grandTotal(grandTotal)
                .build();
    }
}
