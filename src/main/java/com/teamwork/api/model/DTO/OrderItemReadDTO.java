package com.teamwork.api.model.DTO;

import com.teamwork.api.model.OrderItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemReadDTO {
    private Long id;
    private ProductReadDTO productReadDTO;
    private Integer quantity;

    public static OrderItemReadDTO fromOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemReadDTO.builder()
                .id(orderItem.getId())
                .productReadDTO(ProductReadDTO.fromProduct(orderItem.getProduct()))
                .quantity(orderItem.getQuantity())
                .build();
    }
}
