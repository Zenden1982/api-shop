package com.teamwork.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teamwork.api.model.DTO.ImageReadDTO;
import com.teamwork.api.model.Image;
import com.teamwork.api.repository.ConfigOptionRepository;
import com.teamwork.api.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/images")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Images", description = "Загрузка и получение изображений товаров")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ConfigOptionRepository configOptionRepository;

    @Operation(summary = "Загрузить изображение", description = "Загружает файл изображения и создаёт запись для конфигурации")
    @PostMapping("/{configId}")
    public ResponseEntity<Image> create(@PathVariable Long configId, @RequestPart("image") MultipartFile image) {
        Image image2 = new Image();
        imageService.uploadImage(image);
        image2.setImage(image.getOriginalFilename());
        image2.setConfig(configOptionRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + configId)));
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.create(image2));
    }

    @Operation(summary = "Получить изображение", description = "Возвращает DTO с информацией об изображении по id")
    @GetMapping("/{id}")
    public ResponseEntity<ImageReadDTO> read(@PathVariable Long id) {
        ImageReadDTO dto = imageService.read(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Список изображений", description = "Возвращает список изображений по id продукта")
    @GetMapping("/all")
    public ResponseEntity<List<ImageReadDTO>> readAll(@RequestParam(required = true) Long configId) {
        return ResponseEntity.ok(imageService.readAll(configId));
    }

    @Operation(summary = "Удалить изображение", description = "Удаляет изображение по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}