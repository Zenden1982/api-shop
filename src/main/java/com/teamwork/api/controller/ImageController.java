package com.teamwork.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teamwork.api.model.Image;
import com.teamwork.api.model.DTO.ImageReadDTO;
import com.teamwork.api.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Operation(summary = "Загрузить изображение", description = "Загружает файл изображения и создаёт запись для конфигурации")
    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Image> create(@PathVariable Long productId,
            @RequestPart("image") MultipartFile image) {
        Image image2 = new Image();
        imageService.uploadImage(image);
        image2.setImage(image.getOriginalFilename());
        image2.setProduct(productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId)));
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.create(image2));
    }

    @Operation(summary = "Получить изображение", description = "Возвращает DTO с информацией об изображении по id")
    @GetMapping("/{id}")
    public ResponseEntity<ImageReadDTO> read(@PathVariable Long id) {
        ImageReadDTO dto = imageService.read(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Список изображений", description = "Возвращает список изображений по id продукта")
    @GetMapping("/allByProduct")
    public ResponseEntity<ImageReadDTO> readAll(@RequestParam(required = true) Long productId) {
        return ResponseEntity.ok(imageService.readImageByProductId(productId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить изображение", description = "Удаляет изображение по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}