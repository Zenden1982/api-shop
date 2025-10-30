package com.teamwork.api.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.Payment;
import com.teamwork.api.model.DTO.PaymentStatusDTO;
import com.teamwork.api.service.PaymentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create/{orderId}")
    public ResponseEntity<Payment> createPayment(@PathVariable Long orderId) {
        Payment payment = paymentService.createPayment(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/status/{transactionId}")
    public ResponseEntity<PaymentStatusDTO> getStatus(@PathVariable String transactionId) {
        PaymentStatusDTO status = paymentService.getPaymentStatus(transactionId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/capture/{transactionId}")
    public ResponseEntity<Payment> capture(@PathVariable String transactionId,
            @RequestParam("amount") BigDecimal amount) {
        Payment payment = paymentService.capturePayment(transactionId, amount);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/cancel/{transactionId}")
    public ResponseEntity<Payment> cancel(@PathVariable String transactionId) {
        Payment payment = paymentService.cancelPayment(transactionId);
        return ResponseEntity.ok(payment);
    }

}
