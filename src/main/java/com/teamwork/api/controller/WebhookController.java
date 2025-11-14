package com.teamwork.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.service.WebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Webhooks", description = "Получение вебхуков.")
public class WebhookController {

    private final WebhookService webhookService;

    @Operation(summary = "Обработчик вебхуков от YooKassa", description = "Принимает POST-уведомления от YooKassa. Этот эндпоинт должен быть публично доступен.")
    @PostMapping("/yookassa")
    public ResponseEntity<Void> handleYooKassaWebhook(@RequestBody String payload) {
        log.info("Получен вебхук от YooKassa");
        // Здесь может быть логика проверки IP-адреса или подписи
        webhookService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Зарегистрировать новый вебхук в YooKassa", description = "Доступно только администраторам.")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<String> registerWebhook(@RequestParam String event) {
        String webhookId = webhookService.registerWebhook(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(webhookId);
    }

    @Operation(summary = "Получить список зарегистрированных вебхуков", description = "Доступно только администраторам.")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<?> listWebhooks() {
        return ResponseEntity.ok(webhookService.getAllWebhooks());
    }

    @Operation(summary = "Удалить вебхук из YooKassa", description = "Доступно только администраторам.")
    @DeleteMapping("/{webhookId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Void> deleteWebhook(@PathVariable String webhookId) {
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.noContent().build();
    }
}
