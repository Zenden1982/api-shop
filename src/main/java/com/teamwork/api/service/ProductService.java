package com.teamwork.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.teamwork.api.model.Product;
import com.teamwork.api.model.DTO.ProductCreateUpdateDTO;
import com.teamwork.api.model.DTO.ProductReadDTO;
import com.teamwork.api.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductReadDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductReadDTO::fromProduct);
    }

    public ProductReadDTO findById(Long id) {
        return productRepository.findById(id)
                .map(ProductReadDTO::fromProduct).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public ProductReadDTO create(ProductCreateUpdateDTO dto) {
        Product product = ProductCreateUpdateDTO.toProduct(dto);
        Product savedProduct = productRepository.save(product);
        return ProductReadDTO.fromProduct(savedProduct);
    }

    @Transactional
    public ProductReadDTO update(Long id, ProductCreateUpdateDTO dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setVersion(dto.getVersion());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setStockQuantity(dto.getStockQuantity());
        existingProduct.setIsAvailable(dto.getIsAvailable());
        existingProduct.setImageUrl(dto.getImageUrl());

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductReadDTO.fromProduct(updatedProduct);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
