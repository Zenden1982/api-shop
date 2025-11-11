package com.teamwork.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.ConfigOption;
import com.teamwork.api.model.OptionChoice;
import com.teamwork.api.model.DTO.OptionChoiceDTO;
import com.teamwork.api.repository.ConfigOptionRepository;
import com.teamwork.api.repository.OptionChoiceRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/option-choices")
@SecurityRequirement(name = "BearerAuth")
@Validated
@io.swagger.v3.oas.annotations.tags.Tag(name = "OptionChoices", description = "Управление вариантами опций (OptionChoice).\n\nИнструкция: 1) Зарегистрируйтесь (/api/v1/users POST). 2) Выполните логин (/api/v1/users/login POST) и получите JWT. 3) Для защищённых эндпоинтов добавьте заголовок Authorization: Bearer <JWT>. 4) Создавать/изменять варианты может админ через этот контроллер.")
public class OptionChoiceController {

    private final OptionChoiceRepository optionChoiceRepository;
    private final ConfigOptionRepository configOptionRepository;

    public OptionChoiceController(OptionChoiceRepository optionChoiceRepository,
            ConfigOptionRepository configOptionRepository) {
        this.optionChoiceRepository = optionChoiceRepository;
        this.configOptionRepository = configOptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<OptionChoiceDTO>> getAll() {
        List<OptionChoiceDTO> list = optionChoiceRepository.findAll().stream()
                .map(OptionChoiceDTO::fromOptionChoice).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionChoiceDTO> getById(@PathVariable @Positive Long id) {
        return optionChoiceRepository.findById(id).map(OptionChoiceDTO::fromOptionChoice)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OptionChoiceDTO> create(@RequestBody @Valid OptionChoiceDTO dto) {
        if (dto.getOptionId() == null) {
            return ResponseEntity.badRequest().build();
        }
        ConfigOption option = configOptionRepository.findById(dto.getOptionId()).orElse(null);
        if (option == null) {
            return ResponseEntity.badRequest().build();
        }
        OptionChoice entity = new OptionChoice();
        entity.setChoiceValue(dto.getValue());
        entity.setPrice(dto.getPrice());
        entity.setOption(option);

        OptionChoice saved = optionChoiceRepository.save(entity);
        return ResponseEntity.created(URI.create("/api/v1/option-choices/" + saved.getId()))
                .body(OptionChoiceDTO.fromOptionChoice(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionChoiceDTO> update(@PathVariable @Positive Long id,
            @RequestBody @Valid OptionChoiceDTO dto) {
        return optionChoiceRepository.findById(id).map(existing -> {
            if (dto.getValue() != null)
                existing.setChoiceValue(dto.getValue());
            if (dto.getPrice() != null)
                existing.setPrice(dto.getPrice());
            if (dto.getOptionId() != null) {
                ConfigOption option = configOptionRepository.findById(dto.getOptionId()).orElse(null);
                if (option == null)
                    return null; // will be handled below as bad request
                existing.setOption(option);
            }
            OptionChoice saved = optionChoiceRepository.save(existing);
            return OptionChoiceDTO.fromOptionChoice(saved);
        }).map(ResponseEntity::ok)
                .orElseGet(() -> {
                    // if we returned null from the mapper because of bad optionId -> return bad
                    // request
                    if (dto.getOptionId() != null && configOptionRepository.findById(dto.getOptionId()).isEmpty())
                        return ResponseEntity.badRequest().build();
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!optionChoiceRepository.existsById(id))
            return ResponseEntity.notFound().build();
        optionChoiceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
