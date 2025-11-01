package com.teamwork.api.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.teamwork.api.model.Image;
import com.teamwork.api.model.Product;
import com.teamwork.api.model.Enum.Version;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReadDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Version version;
    private String description;
    private Integer stockQuantity;
    private Boolean isAvailable;
    private List<Image> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Преобразует сущность Product в DTO для чтения.
     */
    public static ProductReadDTO fromProduct(Product product) {
        if (product == null)
            return null;
        return ProductReadDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .version(product.getVersion())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .isAvailable(product.getIsAvailable())
                .images(product.getImages())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
