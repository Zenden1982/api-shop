package com.teamwork.api.model.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.User;
import com.teamwork.api.model.Enum.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateUpdateDTO {

    private Long userId;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private String phoneNumber;

    /**
     * Преобразует DTO в сущность Order. Поля id и временные метки не устанавливаются.
     */
    public static Order toOrder(OrderCreateUpdateDTO dto, User user, List<OrderItemDTO> orderItems) {
        if (dto == null) return null;
        Order order = new Order();
        order.setUser(user);
        order.setItems(orderItems.stream().map(OrderItemDTO::toOrderItem).toList());
        order.setTotalPrice(dto.getTotalPrice());
        order.setStatus(dto.getStatus());
        order.setShippingAddress(dto.getShippingAddress());
        order.setPhoneNumber(dto.getPhoneNumber());
        return order;
    }

}
