package com.teamwork.api.model.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemQuantityRequestDTO {

    private Integer quantity;
}
