package com.teamwork.api.model.DTO;

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
    private Long selectedOptionId;
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
}
