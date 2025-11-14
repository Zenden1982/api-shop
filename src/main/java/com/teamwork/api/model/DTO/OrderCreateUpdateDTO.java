package com.teamwork.api.model.DTO;

import com.teamwork.api.model.Enum.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateUpdateDTO {

    @NotNull(message = "ID пользователя не может быть пустым")
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotNull(message = "Необходимо выбрать вариант товара")
    private Long selectedOptionId;

    @NotBlank(message = "Адрес доставки не может быть пустым")
    private String shippingAddress;

    // Статус обычно устанавливается сервером, но если его можно задавать, то
    // NotNull.
    // Если статус устанавливается только при обновлении, можно использовать группы
    // валидации.
    private OrderStatus status;

    @NotBlank(message = "Номер телефона не может быть пустым")
    private String phoneNumber;
}
