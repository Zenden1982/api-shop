package com.teamwork.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.DTO.PaymentResponseDTO;
import com.teamwork.api.model.DTO.PaymentStatusDTO;
import com.teamwork.api.model.Enum.PaymentStatus;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;
import ru.loolzaaa.youkassa.pojo.Amount;
import ru.loolzaaa.youkassa.pojo.Confirmation;
import ru.loolzaaa.youkassa.pojo.Currency;
import ru.loolzaaa.youkassa.processors.PaymentProcessor;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${payment.yookassa.shop-id}")
    private String shopId;

    @Value("${payment.yookassa.secret-key}")
    private String secretKey;

    @Value("${payment.yookassa.return-url}")
    private String returnUrl;

    @Transactional
    public PaymentResponseDTO createPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        PaymentProcessor paymentProcessor = new PaymentProcessor(client);

        ru.loolzaaa.youkassa.model.Payment yooPayment = paymentProcessor.create(
                ru.loolzaaa.youkassa.model.Payment.builder()
                        .amount(Amount.builder()
                                .value(order.getTotalPrice().toString())
                                .currency(Currency.RUB)
                                .build())
                        .description("Order #" + orderId)
                        .confirmation(Confirmation.builder()
                                .type(Confirmation.Type.REDIRECT)
                                .returnUrl(returnUrl)
                                .build())
                        .capture(true)
                        .build(),
                UUID.randomUUID().toString()
        );

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .currency("RUB")
                .paymentDate(LocalDateTime.now())
                .status(mapYooKassaStatus(yooPayment.getStatus()))
                .transactionId(yooPayment.getId())
                .build();

        paymentRepository.save(payment);

        String confirmationUrl = yooPayment.getConfirmation() != null
                ? yooPayment.getConfirmation().getConfirmationUrl()
                : null;

        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .orderId(orderId)
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .confirmationUrl(confirmationUrl)
                .build();
    }

    @Transactional
    public PaymentStatusDTO getPaymentStatus(String transactionId) {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        PaymentProcessor paymentProcessor = new PaymentProcessor(client);
        ru.loolzaaa.youkassa.model.Payment yooPayment = paymentProcessor.findById(transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(mapYooKassaStatus(yooPayment.getStatus()));
        paymentRepository.save(payment);

        return PaymentStatusDTO.builder()
                .orderId(payment.getOrder().getId())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount().toString())
                .build();
    }

    @Transactional
    public Payment capturePayment(String transactionId, BigDecimal amount) {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        PaymentProcessor paymentProcessor = new PaymentProcessor(client);
        
        ru.loolzaaa.youkassa.model.Payment yooPayment = paymentProcessor.capture(
                transactionId,
                ru.loolzaaa.youkassa.model.Payment.builder()
                        .amount(Amount.builder()
                                .value(amount.toString())
                                .currency(Currency.RUB)
                                .build())
                        .build(),
                UUID.randomUUID().toString()
        );

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(mapYooKassaStatus(yooPayment.getStatus()));
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment cancelPayment(String transactionId) {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        PaymentProcessor paymentProcessor = new PaymentProcessor(client);
        ru.loolzaaa.youkassa.model.Payment yooPayment = paymentProcessor.cancel(
                transactionId,
                UUID.randomUUID().toString()
        );

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(mapYooKassaStatus(yooPayment.getStatus()));
        return paymentRepository.save(payment);
    }

    private PaymentStatus mapYooKassaStatus(String yooStatus) {
        return switch (yooStatus.toUpperCase()) {
            case "PENDING" -> PaymentStatus.PENDING;
            case "WAITING_FOR_CAPTURE" -> PaymentStatus.WAITING_FOR_CAPTURE;
            case "SUCCEEDED" -> PaymentStatus.SUCCEEDED;
            case "CANCELED" -> PaymentStatus.CANCELED;
            default -> PaymentStatus.PENDING;
        };
    }
}
