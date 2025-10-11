package com.teamwork.api.service;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.teamwork.api.model.Order;
import com.teamwork.api.model.DTO.CreatePaymentRequestDTO;
import com.teamwork.api.model.DTO.PaymentResponseDTO;
import com.teamwork.api.model.YooKassaModel.YooKassaAmount;
import com.teamwork.api.model.YooKassaModel.YooKassaConfirmation;
import com.teamwork.api.model.YooKassaModel.YooKassaCustomer;
import com.teamwork.api.model.YooKassaModel.YooKassaPaymentRequest;
import com.teamwork.api.model.YooKassaModel.YooKassaPaymentResponse;
import com.teamwork.api.model.YooKassaModel.YooKassaReceipt;
import com.teamwork.api.model.YooKassaModel.YooKassaReceiptItem;
import com.teamwork.api.repository.OrderRepository;
import com.teamwork.api.repository.PaymentRepository;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class PaymentService {

        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        @Value("${payment.yookassa.shop-id}")
        private String shopId;

        @Value("${payment.yookassa.secret-key}")
        private String secretKey;

        @Value("${payment.yookassa.return-url}")
        private String returnUrl;

        @Transactional
        public PaymentResponseDTO createPayment(CreatePaymentRequestDTO request) {
                return null;
        }

        private YooKassaPaymentResponse createYooKassaPayment(Order order) {
                RestTemplate restTemplate = new RestTemplate();

                // Формирование заголовков с Basic Auth
                String auth = shopId + ":" + secretKey;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Basic " + encodedAuth);
                headers.set("Idempotence-Key", UUID.randomUUID().toString());

                // Формирование тела запроса
                YooKassaPaymentRequest paymentRequest = YooKassaPaymentRequest.builder()
                                .amount(new YooKassaAmount(
                                                order.getTotalPrice().toString(),
                                                "RUB"))
                                .capture(true)
                                .confirmation(new YooKassaConfirmation(
                                                "redirect",
                                                returnUrl))
                                .description("Оплата заказа №" + order.getId())
                                .metadata(Map.of(
                                                "order_id", order.getId().toString(),
                                                "user_id", order.getUser().getId().toString()))
                                .receipt(buildReceipt(order))
                                .build();

                HttpEntity<YooKassaPaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

                ResponseEntity<YooKassaPaymentResponse> response = restTemplate.exchange(
                                "https://api.yookassa.ru/v3/payments",
                                HttpMethod.POST,
                                request,
                                YooKassaPaymentResponse.class);

                if (response.getStatusCode() != HttpStatus.OK) {
                        throw new RuntimeException("Ошибка при создании платежа в ЮKassa");
                }

                return response.getBody();
        }

        private YooKassaReceipt buildReceipt(Order order) {
                List<YooKassaReceiptItem> items = order.getItems().stream()
                                .map(item -> YooKassaReceiptItem.builder()
                                                .description(item.getProduct().getName())
                                                .quantity(String.valueOf(item.getQuantity()))
                                                .amount(new YooKassaAmount(
                                                                String.valueOf(item.getPrice()),
                                                                "RUB"))
                                                .vatCode(1) // без НДС
                                                .paymentMode("full_payment")
                                                .paymentSubject("commodity")
                                                .build())
                                .collect(Collectors.toList());

                return YooKassaReceipt.builder()
                                .customer(new YooKassaCustomer(
                                                order.getUser().getEmail(),
                                                order.getUser().getPhoneNumber()))
                                .items(items)
                                .build();
        }
}
