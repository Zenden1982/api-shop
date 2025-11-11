package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.OptionChoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionChoiceDTO {
    private Long id;

    @NotBlank(message = "Значение варианта не может быть пустым")
    private String value;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", inclusive = true, message = "Цена не может быть отрицательной")
    private BigDecimal price;

    @NotNull(message = "ID родительской опции не может быть пустым")
    @Positive(message = "ID родительской опции должен быть положительным числом")
    private Long optionId;

    public static OptionChoiceDTO fromOptionChoice(OptionChoice c) {
        if (c == null)
            return null;
        return OptionChoiceDTO.builder()
                .id(c.getId())
                .value(c.getChoiceValue())
                .price(c.getPrice())
                .optionId(c.getOption() != null ? c.getOption().getId() : null)
                .build();
    }
}
