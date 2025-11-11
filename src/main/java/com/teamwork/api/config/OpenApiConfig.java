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
                String guide = """
                                # Быстрый старт API

                                1) Регистрация

                                - Endpoint: POST /api/v1/users
                                - Тело (пример JSON):

                                        {
                                                "username": "ivan",
                                                "firstName": "Ivan",
                                                "lastName": "Petrov",
                                                "email": "ivan@example.com",
                                                "phoneNumber": "+79991234567",
                                                "password": "Secret123"
                                        }

                                - Результат: 201 Created и объект пользователя (UserReadDTO)

                                2) Аутентификация (получение JWT)

                                - Endpoint: POST /api/v1/users/login
                                - Тело (пример JSON):

                                        {
                                                "username": "ivan",
                                                "password": "Secret123"
                                        }

                                - Результат: 200 OK, в теле возвращается JWT (строка). Скопируйте токен.

                                3) Использование JWT

                                - Для защищённых эндпоинтов добавьте HTTP-заголовок:
                                        Authorization: Bearer <ВАШ_JWT>

                                4) Создание заказа

                                - Endpoint: POST /api/v1/orders
                                - Тело (пример JSON):

                                        {
                                                "userId": 1,
                                                "items": [
                                                        { "selectedOptionIds": [1, 2] }
                                                ],
                                                "shippingAddress": "ул. Ленина, 1, Москва",
                                                "phoneNumber": "+79991234567"
                                        }

                                - Описание полей:
                                        - userId: id пользователя, от имени которого оформляется заказ.
                                        - items: массив позиций, каждая позиция содержит selectedOptionIds.
                                        - shippingAddress: строка с адресом доставки.
                                        - phoneNumber: контактный телефон для доставки.

                                - Результат: 201 Created и объект заказа (OrderReadDTO). Платёж создаётся автоматически и возвращается confirmationUrl, если требуется перенаправление на страницу оплаты.

                                5) Статусы платежа

                                - Endpoint: GET /api/v1/payments/status/{transactionId}
                                - Результат: PaymentStatusDTO с полем status (PENDING / SUCCEEDED / CANCELED / WAITING_FOR_CAPTURE)

                                6) Общие советы

                                - Все защищённые методы помечены схемой BearerAuth в Swagger UI. Нажмите кнопку "Authorize" и вставьте: Bearer <ваш токен>.
                                - Поля, которые не передаются в DTO при обновлении сущности, не будут перезаписаны (partial update поддерживается на backend).

                                """;

                return new OpenAPI()
                                // сервера
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
                                                .title("Окно в Россию")
                                                .description(guide)
                                                .version("0.0.1"));
        }
}
