package com.teamwork.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.service.WebhookService;

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

    @PostMapping("/yookassa")
    public ResponseEntity<Void> handleYooKassaWebhook(@RequestBody String payload,
            @RequestHeader("Content-Type") String contentType) {
        log.info("Received webhook from YooKassa");
        webhookService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerWebhook(@RequestParam String event) {
        String webhookId = webhookService.registerWebhook(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(webhookId);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listWebhooks() {
        return ResponseEntity.ok(webhookService.getAllWebhooks());
    }

    @DeleteMapping("/{webhookId}")
    public ResponseEntity<Void> deleteWebhook(@PathVariable String webhookId) {
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.noContent().build();
    }
}
