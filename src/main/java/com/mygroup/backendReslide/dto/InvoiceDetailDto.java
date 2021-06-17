package com.mygroup.backendReslide.dto;

import com.mygroup.backendReslide.dto.DiscountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailDto {
    private Long id;
    private String productCode;
    private BigDecimal quantity;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;

    private DiscountDto discountApplied;

    private String status; // Invoice detail status
    private String notes;
}
