package com.teamwork.api.model.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
}