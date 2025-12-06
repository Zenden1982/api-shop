package com.teamwork.api.model.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewCreateUpdateDTO {

    @NotNull(message = "ID продукта обязателен")
    private Long productId;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    @Size(max = 2000, message = "Отзыв слишком длинный")
    private String text;

    @NotNull(message = "Рейтинг обязателен")
    @Min(value = 1, message = "Минимальная оценка 1")
    @Max(value = 5, message = "Максимальная оценка 5")
    private Integer rating;
}