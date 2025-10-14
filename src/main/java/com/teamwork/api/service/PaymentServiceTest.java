package com.teamwork.api.service;

import org.springframework.stereotype.Service;

import com.teamwork.api.repository.PaymentRepository;

import lombok.Data;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.model.Payment;
import ru.loolzaaa.youkassa.pojo.Amount;
import ru.loolzaaa.youkassa.pojo.Confirmation;
import ru.loolzaaa.youkassa.pojo.Currency;
import ru.loolzaaa.youkassa.processors.PaymentProcessor;

@Service
@Data
public class PaymentServiceTest {
    private final ApiClient apiClient;
    private final PaymentRepository paymentRepository;

    public Payment createPayment() {
        PaymentProcessor paymentProcessor = new PaymentProcessor(apiClient);
        Payment payment = paymentProcessor.create(Payment.builder()
                .amount(Amount.builder().value("100.00").currency(Currency.RUB).build())
                .description("New payment")
                .confirmation(Confirmation.builder()
                        .type(Confirmation.Type.REDIRECT)
                        .returnUrl("https://www.example.com/return_url")
                        .build())
                .build(), null);
        return payment;
    }

}