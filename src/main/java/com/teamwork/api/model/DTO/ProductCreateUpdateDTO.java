package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateUpdateDTO {

    @NotBlank(message = "Название продукта не может быть пустым")
    @Size(max = 255, message = "Название продукта не должно превышать 255 символов")
    private String name;

    @Size(max = 1000, message = "Описание продукта не должно превышать 1000 символов")
    private String description;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    private BigDecimal price;

    // @URL(message = "Некорректный формат URL изображения")
    private String imageUrl;

    @NotNull(message = "Количество на складе не может быть пустым")
    @Min(value = 0, message = "Количество на складе не может быть отрицательным")
    private Integer stockQuantity;
}