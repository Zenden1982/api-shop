package com.teamwork.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamwork.api.model.DTO.ProductCreateUpdateDTO;
import com.teamwork.api.model.DTO.ProductReadDTO;
import com.teamwork.api.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Управление каталогом продуктов")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Получить список всех продуктов (каталог)")
    public ResponseEntity<Page<ProductReadDTO>> getAllProducts(@PageableDefault(size = 12) Pageable pageable) {
        Page<ProductReadDTO> products = productService.findAll(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о продукте по ID")
    public ResponseEntity<ProductReadDTO> getProductById(@PathVariable @Positive Long id) {
        ProductReadDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новый продукт (только для админа)", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ProductReadDTO> createProduct(@Valid @RequestBody ProductCreateUpdateDTO dto) {
        ProductReadDTO createdProduct = productService.create(dto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить продукт (только для админа)", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<ProductReadDTO> updateProduct(@PathVariable @Positive Long id,
            @Valid @RequestBody ProductCreateUpdateDTO dto) {
        ProductReadDTO updatedProduct = productService.update(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить продукт (только для админа)", security = @SecurityRequirement(name = "BearerAuth"))
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
