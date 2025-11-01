package com.teamwork.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Введите JWT токен")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // ← ДОБАВЬТЕ СЕРВЕРЫ
                .addServersItem(new Server()
                        .url("https://окно-в.рф")
                        .description("Production Server (HTTPS)"))

                .addServersItem(new Server()
                        .url("https://www.окно-в.рф")
                        .description("Production Server with WWW (HTTPS)"))

                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Server (HTTP)"))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Окно в Россию") // ← ЗАМЕНИТЕ НА НАЗВАНИЕ ВАШЕГО ПРИЛОЖЕНИЯ
                        .description("API") // ← ЗАМЕНИТЕ НА ОПИСАНИЕ ВАШЕГО ПРИЛОЖЕНИЯ
                        .version("0.0.1"));
    }
}
