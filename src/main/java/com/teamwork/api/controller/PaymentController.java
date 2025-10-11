package com.teamwork.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.service.PaymentServiceTest;

import lombok.Data;

@RestController
@Data
public class PaymentController {
    private final PaymentServiceTest paymentService;

    @PostMapping("/payments")
    public ResponseEntity<?> createPayment(Double amount, String currency, Long orderId) {
        try {
            return ResponseEntity.ok(paymentService.createPayment(amount, currency, orderId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating payment: " + e.getMessage());
        }
    }
}
