package com.teamwork.api.model.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportRowDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Double height;
    private Double width;
}