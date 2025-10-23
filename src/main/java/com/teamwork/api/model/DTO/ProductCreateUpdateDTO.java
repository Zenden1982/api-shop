package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import com.teamwork.api.model.Product;
import com.teamwork.api.model.Enum.Version;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCreateUpdateDTO {

	private String name;
	private BigDecimal price;
	private Version version;
	private String description;
	private Integer stockQuantity;
	private Boolean isAvailable;
	private String imageUrl;

	/**
	 * Преобразует DTO в сущность Product. Поле id и временные метки не устанавливаются.
	 */
	public static Product toProduct(ProductCreateUpdateDTO dto) {
		if (dto == null) return null;
		Product p = new Product();
		p.setName(dto.getName());
		p.setPrice(dto.getPrice());
		p.setVersion(dto.getVersion());
		p.setDescription(dto.getDescription());
		p.setStockQuantity(dto.getStockQuantity());
		p.setIsAvailable(dto.getIsAvailable());
		p.setImageUrl(dto.getImageUrl());
		return p;
	}

}
