package com.mygroup.backendReslide.dto.request;

import com.mygroup.backendReslide.dto.DiscountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailRequest {
    private Long id;
    private Long invoiceId;
    private String productCode;
    private String productName;
    private BigDecimal priceByUnit; // Price of the item at the time of the purchase
    private BigDecimal quantity;

    private DiscountDto discountApplied;

    private String status; // Invoice detail status
    private String notes;

}
