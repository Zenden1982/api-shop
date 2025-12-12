package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.CartItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {
    private Long id;
    private ProductReadDTO productReadDTO;
    private String productName;
    private Integer quantity;
    private BigDecimal pricePerItem;
    private BigDecimal totalPrice;

    public static CartItemDTO fromCartItem(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productReadDTO(ProductReadDTO.fromProduct(item.getProduct()))
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .pricePerItem(item.getProduct().getPrice())
                .totalPrice(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .build();
    }
}
