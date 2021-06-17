package com.mygroup.backendReslide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    private Long id;
    private String clientCode;
    private String clientName;

    private TransactionDto transaction;

    private String status; // Invoice status

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private BigDecimal paid;
    private BigDecimal owed;

    private List<InvoiceDetailDto> details;
}
