package com.teamwork.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.service.PaymentService;
import com.teamwork.api.service.PaymentServiceTest;

import lombok.Data;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;
import ru.loolzaaa.youkassa.model.Me;
import ru.loolzaaa.youkassa.processors.MeProcessor;

@RestController
@Data
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private final PaymentServiceTest paymentServiceTest;

    @GetMapping("/test/me")
    public String test() {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth("511161", "test_*gYTwB7hO60RaEBrJNbUtJ776gMF5eWSGAiN71fFrTVzE")
                .build();
        MeProcessor meProcessor = new MeProcessor(client);
        Me me = meProcessor.findMe();
        return me.toString();
    }

    @PostMapping("/test/pay")
    public ResponseEntity<?> testPay() {
        return ResponseEntity.ok(paymentServiceTest.createPayment());
    }
}
