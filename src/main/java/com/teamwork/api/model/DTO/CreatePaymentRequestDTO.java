package com.teamwork.api.model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequestDTO {
    private Long userId;
    private List<OrderItemDTO> items;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
}
