package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.Enum.OrderStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreateUpdateDTO {

    @NotNull(message = "userId is required")
    @Positive(message = "userId must be positive")
    private Long userId;

    @NotEmpty(message = "items must not be empty")
    private List<OrderItemDTO> items;

    private String shippingAddress;
    private OrderStatus status;
    private String phoneNumber;

}
