package com.teamwork.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.model.Product;
import com.teamwork.api.model.DTO.ProductCreateUpdateDTO;
import com.teamwork.api.model.DTO.ProductReadDTO;
import com.teamwork.api.repository.ProductRepository;
import com.teamwork.api.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<ProductReadDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> {
                    // подтягиваем средний рейтинг из отзывов
                    Double avg = reviewRepository.findAverageRatingByProductId(product.getId());
                    product.setAverageRating(avg != null ? avg : 0.0);
                    return ProductReadDTO.fromProduct(product);
                });
    }

    @Transactional(readOnly = true)
    public ProductReadDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт с ID " + id + " не найден"));

        Double avg = reviewRepository.findAverageRatingByProductId(product.getId());
        product.setAverageRating(avg != null ? avg : 0.0);

        return ProductReadDTO.fromProduct(product);
    }

    @Transactional
    public ProductReadDTO create(ProductCreateUpdateDTO dto) {
        Product product = new Product();
        mapDtoToEntity(dto, product);
        // при создании пока отзывов нет
        product.setAverageRating(0.0);
        Product savedProduct = productRepository.save(product);
        return ProductReadDTO.fromProduct(savedProduct);
    }

    @Transactional
    public ProductReadDTO update(Long id, ProductCreateUpdateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт с ID " + id + " не найден"));
        mapDtoToEntity(dto, product);

        // пересчитали средний рейтинг из отзывов
        Double avg = reviewRepository.findAverageRatingByProductId(product.getId());
        product.setAverageRating(avg != null ? avg : 0.0);

        Product updatedProduct = productRepository.save(product);
        return ProductReadDTO.fromProduct(updatedProduct);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Продукт с ID " + id + " не найден");
        }
        productRepository.deleteById(id);
    }

    private void mapDtoToEntity(ProductCreateUpdateDTO dto, Product product) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setHeight(dto.getHeight());
        product.setWidth(dto.getWidth());
        product.setColor(dto.getColor());
        product.setStockQuantity(dto.getStockQuantity());
    }
}
