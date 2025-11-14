package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.Product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReadDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    Integer stockQuantity;

    /**
     * Статический метод для преобразования сущности Product в ProductReadDTO.
     * 
     * @param product Сущность продукта из базы данных.
     * @return DTO для отображения клиенту.
     */
    public static ProductReadDTO fromProduct(Product product) {
        if (product == null) {
            return null;
        }
        return ProductReadDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}