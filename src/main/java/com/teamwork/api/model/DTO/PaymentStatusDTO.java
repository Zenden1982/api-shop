package com.teamwork.api.model.DTO;

import com.teamwork.api.model.Enum.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusDTO {

    private Long orderId;
    private PaymentStatus status;

    public String transactionId;
    public String amount;
}
