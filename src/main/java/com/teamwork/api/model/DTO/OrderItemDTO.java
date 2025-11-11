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

    // Цена обычно вычисляется на сервере, поэтому валидация на входе не нужна
    private BigDecimal price;

    // Для входящих запросов: ID выбранных вариантов (OptionChoice)
    @NotEmpty(message = "Каждый товар в заказе должен иметь хотя бы одну выбранную опцию")
    // Дополнительно можно создать кастомную аннотацию, чтобы проверить,
    // что все ID в списке - положительные числа, если это необходимо.
    private List<Long> selectedOptionIds;

    // Для исходящих ответов: полные DTO выбранных вариантов
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
