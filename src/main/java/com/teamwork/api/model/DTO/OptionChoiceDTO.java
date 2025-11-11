package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.OptionChoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionChoiceDTO {
    private Long id;

    @NotBlank(message = "value is required")
    private String value;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "price must be non-negative")
    private BigDecimal price;

    @NotNull(message = "optionId is required")
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
