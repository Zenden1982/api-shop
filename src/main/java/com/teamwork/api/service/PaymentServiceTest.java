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

    public Payment createPayment(Double value, String currency, Long orderId) throws Exception {
        PaymentProcessor paymentProcessor = new PaymentProcessor(apiClient);
        String valueStr = String.valueOf(value);
        return paymentProcessor.create(Payment.builder()
                .amount(Amount.builder().value(valueStr).currency(Currency.RUB).build())
                .description("Оплата заказа")
                .confirmation(Confirmation.builder()
                        .type(Confirmation.Type.REDIRECT)
                        .returnUrl("http://localhost:8080/")
                        .build())
                .build(), null);
    }

}