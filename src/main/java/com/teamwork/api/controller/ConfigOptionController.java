package com.teamwork.api.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
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
import com.teamwork.api.model.DTO.ConfigOptionDTO;
import com.teamwork.api.repository.ConfigOptionRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "BearerAuth")

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
    public ResponseEntity<ConfigOptionDTO> getById(@PathVariable Long id) {
        return configOptionRepository.findById(id).map(ConfigOptionDTO::fromConfigOption)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConfigOptionDTO> create(@RequestBody ConfigOptionDTO dto) {
        ConfigOption entity = new ConfigOption();
        entity.setName(dto.getName());
        if (dto.getChoices() != null) {
            List<OptionChoice> choices = dto.getChoices().stream().map(c -> {
                OptionChoice oc = new OptionChoice();
                oc.setId(c.getId());
                oc.setChoiceValue(c.getValue());
                oc.setPrice(c.getPrice());
                oc.setOption(entity);
                return oc;
            }).collect(Collectors.toList());
            entity.setChoices(choices);
        }
        ConfigOption saved = configOptionRepository.save(entity);
        return ResponseEntity.created(URI.create("/api/v1/config-options/" + saved.getId()))
                .body(ConfigOptionDTO.fromConfigOption(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigOptionDTO> update(@PathVariable Long id, @RequestBody ConfigOptionDTO dto) {
        return configOptionRepository.findById(id).map(existing -> {
            if (dto.getName() != null)
                existing.setName(dto.getName());

            if (dto.getChoices() != null) {
                List<OptionChoice> choices = dto.getChoices().stream().map(c -> {
                    OptionChoice oc = new OptionChoice();
                    oc.setId(c.getId());
                    oc.setChoiceValue(c.getValue());
                    oc.setPrice(c.getPrice());
                    oc.setOption(existing);
                    return oc;
                }).collect(Collectors.toList());
                existing.setChoices(choices);
            }

            ConfigOption saved = configOptionRepository.save(existing);
            return ResponseEntity.ok(ConfigOptionDTO.fromConfigOption(saved));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!configOptionRepository.existsById(id))
            return ResponseEntity.notFound().build();
        configOptionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
