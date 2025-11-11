package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.Enum.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateUpdateDTO {

    private Long userId;
    private List<OrderItemDTO> items;
    private String shippingAddress;
    private OrderStatus status;
    private String phoneNumber;

}
