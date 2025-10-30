package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.Enum.PaymentStatus;
import com.teamwork.api.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String currency;
    private String transactionId;
    private String confirmationUrl;

    public static PaymentResponseDTO fromPayment(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .currency(payment.getCurrency())
                .transactionId(payment.getTransactionId())
                .confirmationUrl(payment.getConfirmationUrl())
                .build();
    }
}