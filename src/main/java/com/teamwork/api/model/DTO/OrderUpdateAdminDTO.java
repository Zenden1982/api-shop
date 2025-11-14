package com.teamwork.api.model.DTO;

import com.teamwork.api.model.Enum.OrderStatus;

import lombok.Data;

@Data
public class OrderUpdateAdminDTO {
    private OrderStatus status;
    private String shippingAddress;
    private String phoneNumber;
}
