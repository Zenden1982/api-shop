package com.teamwork.api.model.DTO;

import java.util.List;

import com.teamwork.api.model.ConfigOption;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigOptionDTO {
    private Long id;

    @NotBlank(message = "Название опции не может быть пустым")
    @Size(max = 100, message = "Название опции не должно превышать 100 символов")
    private String name;

    // Включаем вложенную валидацию для списка вариантов
    @Valid
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
