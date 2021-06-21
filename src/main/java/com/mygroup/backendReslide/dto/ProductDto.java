package com.mygroup.backendReslide.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String brand; // Product brand.
    private String type; // Product type.
    private String code;
    private String name;
    private String measurementType;
    private BigDecimal quantityAvailable;
    private BigDecimal price;
    private String notes;
    private String productStatus; // Product status.
    private Boolean taxExempt; // Product exempt from taxes.
}
