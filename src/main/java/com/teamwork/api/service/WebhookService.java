package com.teamwork.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamwork.api.model.Payment;
import com.teamwork.api.model.Enum.PaymentStatus;
import com.teamwork.api.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;
import ru.loolzaaa.youkassa.client.PaginatedResponse;
import ru.loolzaaa.youkassa.model.Webhook;
import ru.loolzaaa.youkassa.processors.WebhookProcessor;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Value("${payment.yookassa.shop-id}")
    private String shopId;

    @Value("${payment.yookassa.secret-key}")
    private String secretKey;

    @Value("${payment.yookassa.webhook-url}")
    private String webhookUrl;

    @Transactional
    public void processWebhook(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String event = jsonNode.get("event").asText();
            JsonNode paymentObject = jsonNode.get("object");
            String transactionId = paymentObject.get("id").asText();
            String status = paymentObject.get("status").asText();

            log.info("Processing webhook event: {} for transaction: {}", event, transactionId);

            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Payment not found: " + transactionId));

            payment.setStatus(mapYooKassaStatus(status));
            paymentRepository.save(payment);

            log.info("Payment status updated: {} -> {}", transactionId, status);
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            throw new RuntimeException("Webhook processing failed", e);
        }
    }

    public String registerWebhook(String event) {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        WebhookProcessor webhookProcessor = new WebhookProcessor(client);

        Webhook newWebhook = webhookProcessor.create(Webhook.builder()
                .event(event)
                .url(webhookUrl)
                .build(), null);

        log.info("Webhook registered: {} with ID: {}", event, newWebhook.getId());
        return newWebhook.getId();
    }

    public List<Webhook> getAllWebhooks() {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        WebhookProcessor webhookProcessor = new WebhookProcessor(client);
        PaginatedResponse<Webhook> webhooks = webhookProcessor.findAll();

        return webhooks.getItems().stream().collect(Collectors.toList());
    }

    public void deleteWebhook(String webhookId) {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        WebhookProcessor webhookProcessor = new WebhookProcessor(client);
        webhookProcessor.removeById(webhookId, null);

        log.info("Webhook deleted: {}", webhookId);
    }

    public void deleteAllWebhooks() {
        ApiClient client = ApiClientBuilder.newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();

        WebhookProcessor webhookProcessor = new WebhookProcessor(client);
        PaginatedResponse<Webhook> webhooks = webhookProcessor.findAll();

        for (Webhook webhook : webhooks.getItems()) {
            webhookProcessor.removeById(webhook.getId(), null);
            log.info("Deleted webhook: {}", webhook.getId());
        }
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
