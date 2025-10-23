package com.teamwork.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Получить все продукты с пагинацией.
     */
    @GetMapping
    public ResponseEntity<Page<ProductReadDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    /**
     * Получить продукт по ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductReadDTO> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создать новый продукт.
     */
    @PostMapping
    public ResponseEntity<ProductReadDTO> createProduct(@RequestBody ProductCreateUpdateDTO dto) {
        ProductReadDTO createdProduct = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Обновить существующий продукт.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductReadDTO> updateProduct(@PathVariable Long id, @RequestBody ProductCreateUpdateDTO dto) {
        ProductReadDTO updatedProduct = productService.update(id, dto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Удалить продукт по ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
