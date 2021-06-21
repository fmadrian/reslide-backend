package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
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

    private List<InvoiceDetailResponse> details;
}
