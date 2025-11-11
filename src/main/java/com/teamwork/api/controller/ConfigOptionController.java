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
import com.teamwork.api.model.DTO.ConfigOptionDTO;
import com.teamwork.api.repository.ConfigOptionRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@Validated
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "ConfigOptions", description = "Управление наборами конфигураций и их вариантами (ConfigOption / OptionChoice).\n\nИнструкция: 1) Зарегистрируйтесь (/api/v1/users POST). 2) Выполните логин (/api/v1/users/login POST) и получите JWT. 3) Для защищённых эндпоинтов добавьте заголовок Authorization: Bearer <JWT>. 4) Админские операции (создание/изменение/удаление) доступны только с ролью ADMIN.")
@RequestMapping("/api/v1/config-options")
public class ConfigOptionController {

    private final ConfigOptionRepository configOptionRepository;

    public ConfigOptionController(ConfigOptionRepository configOptionRepository) {
        this.configOptionRepository = configOptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<ConfigOptionDTO>> getAll() {
        List<ConfigOptionDTO> list = configOptionRepository.findAll().stream().map(ConfigOptionDTO::fromConfigOption)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigOptionDTO> getById(@PathVariable @Positive Long id) {
        return configOptionRepository.findById(id).map(ConfigOptionDTO::fromConfigOption)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConfigOptionDTO> create(@RequestBody @Valid ConfigOptionDTO configOptionDTO) {
        ConfigOption configOption = configOptionRepository.save(configOptionDTO.toConfigOption());
        return ResponseEntity.created(URI.create("/api/v1/config-options/" + configOption.getId()))
                .body(ConfigOptionDTO.fromConfigOption(configOption));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigOptionDTO> update(@PathVariable @Positive Long id,
            @RequestBody @Valid ConfigOptionDTO configOptionDTO) {
        ConfigOption configOption = configOptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ConfigOption not found"));
        configOption.updateFromDTO(configOptionDTO);
        configOptionRepository.save(configOption);
        return ResponseEntity.ok(ConfigOptionDTO.fromConfigOption(configOption));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!configOptionRepository.existsById(id))
            return ResponseEntity.notFound().build();
        configOptionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
