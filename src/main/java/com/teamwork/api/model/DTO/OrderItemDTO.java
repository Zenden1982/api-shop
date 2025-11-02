package com.teamwork.api.model.DTO;

import com.teamwork.api.model.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private Integer quantity;

    /**
     * Преобразует OrderItem в OrderItemDTO.
     */
    public static OrderItemDTO fromOrderItem(OrderItem orderItem) {
        if (orderItem == null)
            return null;
        return OrderItemDTO.builder()
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .build();
    }

    /**
     * Преобразует OrderItemDTO в OrderItem.
     */
    public static OrderItem toOrderItem(OrderItemDTO dto) {
        if (dto == null)
            return null;
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        // Связь с продуктом должна быть установлена отдельно
        return orderItem;
    }
}