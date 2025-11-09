package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.ConfigOption;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigOptionDTO {
    private Long id;
    private String name;
    private List<OptionChoiceDTO> choices;

    public static ConfigOptionDTO fromConfigOption(ConfigOption o) {
        if (o == null)
            return null;
        return ConfigOptionDTO.builder()
                .id(o.getId())
                .name(o.getName())
                .choices(
                        o.getChoices() != null ? o.getChoices().stream().map(OptionChoiceDTO::fromOptionChoice).toList()
                                : null)
                .build();
    }
}
