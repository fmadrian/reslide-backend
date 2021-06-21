package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.dto.DiscountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailResponse {
    private Long id;
    private String productCode;
    private String productName;
    private BigDecimal priceByUnit; // Price of the item at the time of the purchase
    private BigDecimal quantity;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private Boolean taxExempt; // Product exempt from taxes at the moment of the transaction.

    private DiscountDto discountApplied;

    private String status; // Invoice detail status
    private String notes;

}
