package com.mygroup.backendReslide.dto.request;

import com.mygroup.backendReslide.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
    private Long id;
    private String clientCode;
    private String clientName;

    private TransactionDto transaction;

    private String status; // Invoice status

    private List<InvoiceDetailRequest> details;
}
