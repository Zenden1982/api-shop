package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.Enum.OrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Заказ должен содержать хотя бы один товар")
    @Valid // Включаем вложенную валидацию для каждого элемента списка
    private List<OrderItemDTO> items;

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
