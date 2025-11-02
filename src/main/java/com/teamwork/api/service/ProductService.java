package com.teamwork.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<ProductReadDTO> findAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
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
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existingProduct.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            existingProduct.setPrice(dto.getPrice());
        }

        if (dto.getVersion() != null) {
            existingProduct.setVersion(dto.getVersion());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            existingProduct.setDescription(dto.getDescription());
        }

        if (dto.getStockQuantity() != null) {
            existingProduct.setStockQuantity(dto.getStockQuantity());
        }

        if (dto.getIsAvailable() != null) {
            existingProduct.setIsAvailable(dto.getIsAvailable());
        }

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
