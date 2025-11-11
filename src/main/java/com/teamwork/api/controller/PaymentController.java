package com.teamwork.api.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.Payment;
import com.teamwork.api.model.DTO.PaymentStatusDTO;
import com.teamwork.api.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Payments", description = "Создание платежей и проверка статусов.\n\nИнструкция: 1) Создайте заказ и получите его id. 2) Выполните логин и добавьте Authorization: Bearer <JWT>. 3) Вызовите POST /api/v1/payments/create/{orderId} для создания платежа; в ответ может прийти confirmationUrl для редиректа. 4) Используйте GET /api/v1/payments/status/{transactionId} для проверки статуса.")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Создать платёж для заказа", description = "Создаёт платёж через YooKassa и возвращает информацию c confirmationUrl (если нужно перенаправление).")
    @PostMapping("/create/{orderId}")
    public ResponseEntity<Payment> createPayment(@PathVariable @Positive Long orderId) {
        Payment payment = paymentService.createPayment(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @Operation(summary = "Получить статус платежа", description = "Возвращает статус платежа по transactionId. Возвращаемая структура: PaymentStatusDTO.")
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<PaymentStatusDTO> getStatus(@PathVariable String transactionId) {
        PaymentStatusDTO status = paymentService.getPaymentStatus(transactionId);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Захват платежа", description = "Захватывает ранее авторизованный платёж. Передайте transactionId и amount в query-параметре.")
    @PostMapping("/capture/{transactionId}")
    public ResponseEntity<Payment> capture(@PathVariable String transactionId,
            @RequestParam("amount") @DecimalMin("0.01") BigDecimal amount) {
        Payment payment = paymentService.capturePayment(transactionId, amount);
        return ResponseEntity.ok(payment);
    }

    // @PostMapping("/cancel/{transactionId}")
    // public ResponseEntity<Payment> cancel(@PathVariable String transactionId) {
    // Payment payment = paymentService.cancelPayment(transactionId);
    // return ResponseEntity.ok(payment);
    // }

}
