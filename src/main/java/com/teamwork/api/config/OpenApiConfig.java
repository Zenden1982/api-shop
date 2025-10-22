package com.teamwork.api.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
    info = @Info(
        title = "Digital Library API",
        version = "1.0",
        description = "API для проекта 'Цифровая полка'"
    )
)
// Описываем саму схему безопасности для JWT
@SecurityScheme(
    name = "bearerAuth", // Имя схемы, которое мы будем использовать для ссылок
    description = "JWT токен для аутентификации",
    scheme = "bearer", // Стандартная схема для JWT
    type = SecuritySchemeType.HTTP, // Тип схемы - HTTP
    bearerFormat = "JWT", // Формат токена
    in = SecuritySchemeIn.HEADER // Где находится токен - в заголовке
)
@Configuration
public class OpenApiConfig {
    // Этот класс может быть пустым, важны только аннотации выше
}