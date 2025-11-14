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
                                # Быстрый старт по API "Окно в Россию"

                                Это API для управления интернет-магазином штор. Оно позволяет пользователям просматривать каталог, управлять корзиной, оформлять заказы и оплачивать их. Администраторы могут управлять каталогом товаров, пользователями и заказами.

                                ---

                                ## 1. Аутентификация

                                ### 1.1. Регистрация нового пользователя
                                - **Endpoint:** `POST /api/v1/users/register`
                                - **Описание:** Создает нового пользователя с ролью `ROLE_USER` и личную корзину для него.
                                - **Тело запроса:**
                                  ```
                                  {
                                    "username": "ivan",
                                    "firstName": "Ivan",
                                    "lastName": "Petrov",
                                    "email": "ivan@example.com",
                                    "phoneNumber": "+79991234567",
                                    "password": "SecretPassword123"
                                  }
                                  ```

                                ### 1.2. Вход и получение JWT
                                - **Endpoint:** `POST /api/v1/users/login`
                                - **Описание:** Аутентифицирует пользователя и возвращает JWT.
                                - **Тело запроса:**
                                  ```
                                  {
                                    "username": "ivan",
                                    "password": "SecretPassword123"
                                  }
                                  ```
                                - **Результат:** `200 OK` с JWT в теле ответа. **Скопируйте этот токен.**

                                ### 1.3. Использование JWT
                                - Для доступа к защищенным эндпоинтам используйте кнопку **"Authorize"** в правом верхнем углу и вставьте ваш токен в формате `Bearer <ВАШ_JWT>`.

                                ---

                                ## 2. Основной сценарий покупки (для пользователя)

                                ### 2.1. Просмотр каталога
                                - **Endpoint:** `GET /api/v1/products`
                                - **Описание:** Получить список доступных товаров (штор). Этот эндпоинт публичен.

                                ### 2.2. Добавление товара в корзину
                                - **Endpoint:** `POST /api/v1/cart/items` **(требует аутентификации)**
                                - **Описание:** Добавляет указанный продукт в вашу корзину.
                                - **Тело запроса:**
                                  ```
                                  {
                                    "productId": 1,
                                    "quantity": 2
                                  }
                                  ```

                                ### 2.3. Просмотр корзины
                                - **Endpoint:** `GET /api/v1/cart` **(требует аутентификации)**
                                - **Описание:** Посмотреть текущее содержимое своей корзины.

                                ### 2.4. Оформление заказа
                                - **Endpoint:** `POST /api/v1/orders/from-cart` **(требует аутентификации)**
                                - **Описание:** Создает заказ на основе товаров в корзине. После успешного создания заказа корзина очищается, и инициируется процесс оплаты.
                                - **Тело запроса:**
                                  ```
                                  {
                                    "shippingAddress": "ул. Ленина, 1, Москва",
                                    "phoneNumber": "+79991234567"
                                  }
                                  ```
                                - **Результат:** `201 Created` с информацией о заказе, включая детали платежа (например, `confirmationUrl` для перехода на страницу YooKassa).

                                ---

                                ## 3. Администрирование (требуется роль `ROLE_ADMIN`)

                                Администраторы имеют доступ к дополнительным эндпоинтам для управления:
                                - **Пользователями:** `GET, PUT, DELETE /api/v1/users/{id}`, `POST /api/v1/users/{id}/roles`
                                - **Продуктами:** `POST, PUT, DELETE /api/v1/products`
                                - **Заказами:** `GET /api/v1/orders`, `PUT /api/v1/orders/{id}`
                                - **Платежами:** `POST /api/v1/payments/capture/{id}`, `POST /api/v1/payments/cancel/{id}`
                                - **Вебхуками:** `POST, GET, DELETE /api/v1/webhooks`

                                """;

                return new OpenAPI()
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
                                                .title("API \"Окно в Россию\"")
                                                .description(guide)
                                                .version("1.0.0"));
        }
}
