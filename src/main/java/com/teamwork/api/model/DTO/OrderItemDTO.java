package com.teamwork.api.model.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.teamwork.api.model.OrderItem;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;

    private BigDecimal price;

    // For incoming requests: ids of selected OptionChoice
    @NotEmpty(message = "selectedOptionIds must not be empty")
    private List<Long> selectedOptionIds;
    // For responses: full DTOs of selected choices
    private List<OptionChoiceDTO> selectedOptions;

    /** Преобразует OrderItem в OrderItemDTO. */
    public static OrderItemDTO fromOrderItem(OrderItem orderItem) {
        if (orderItem == null)
            return null;
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .price(orderItem.getPrice())
                .selectedOptions(orderItem.getSelectedOptions() != null
                        ? orderItem.getSelectedOptions().stream().map(OptionChoiceDTO::fromOptionChoice).toList()
                        : null)
                .build();
    }

    /**
     * Преобразует OrderItemDTO в OrderItem. Связи с OptionChoice должны
     * устанавливаться в сервисе (по id).
     */
    public static OrderItem toOrderItem(OrderItemDTO dto) {
        if (dto == null)
            return null;
        OrderItem item = new OrderItem();
        item.setPrice(dto.getPrice());
        // selectedOptions / order linkage should be resolved by service layer
        return item;
    }
}