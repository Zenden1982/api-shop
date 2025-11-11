package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.ConfigOption;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigOptionDTO {
    private Long id;

    @NotBlank(message = "name is required")
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

    public ConfigOption toConfigOption() {
        ConfigOption configOption = new ConfigOption();
        configOption.setId(this.id);
        configOption.setName(this.name);
        return configOption;
    }
}
