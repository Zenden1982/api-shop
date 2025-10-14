package com.teamwork.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.PaymentRepository;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.UserRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        @Value("${payment.yookassa.return-url}")
        private String returnUrl;

}
