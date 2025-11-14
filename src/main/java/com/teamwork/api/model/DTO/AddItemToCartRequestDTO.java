package com.teamwork.api.model.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO для запроса на добавление товара в корзину.
 * Используется в теле POST запроса на /api/v1/cart/items.
 */
@Data
public class AddItemToCartRequestDTO {

    /**
     * Уникальный идентификатор продукта (Product), который нужно добавить.
     */
    @NotNull(message = "ID продукта не может быть пустым")
    @Positive(message = "ID продукта должен быть положительным числом")
    private Long productId;

    /**
     * Количество единиц товара, которое нужно добавить.
     */
    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть не меньше 1")
    private Integer quantity;
}
