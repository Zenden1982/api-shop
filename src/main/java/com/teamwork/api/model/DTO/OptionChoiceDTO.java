package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.OptionChoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionChoiceDTO {
    private Long id;
    private String value;
    private BigDecimal price;
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
