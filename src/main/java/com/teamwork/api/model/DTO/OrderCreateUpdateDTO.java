package com.teamwork.api.model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateUpdateDTO {

    @NotBlank(message = "Адрес доставки не может быть пустым")
    private String shippingAddress;

    @NotBlank(message = "Номер телефона не может быть пустым")
    private String phoneNumber;
}
