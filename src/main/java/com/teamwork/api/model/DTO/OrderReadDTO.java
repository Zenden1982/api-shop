package com.teamwork.api.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.teamwork.api.model.Enum.OrderStatus;
import com.teamwork.api.model.Order;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderReadDTO {

    private Long id;
    private UserReadDTO user;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private String phoneNumber;
    private List<OrderItemReadDTO> orderItems;
    private PaymentResponseDTO payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Преобразует сущность Order в DTO для чтения.
     */
    public static OrderReadDTO fromOrder(Order order) {
        if (order == null)
            return null;
        return OrderReadDTO.builder()
                .id(order.getId())
                .user(UserReadDTO.toUserReadDTO(order.getUser()))
                .orderItems(order.getOrderItems().stream().map(OrderItemReadDTO::fromOrderItem).toList())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .phoneNumber(order.getPhoneNumber())
                .payment(PaymentResponseDTO.fromPayment(order.getPayment()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
