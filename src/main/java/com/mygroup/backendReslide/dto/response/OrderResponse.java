package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private TransactionDto transaction;
    private String providerName;
    private String providerCode;
    private String expectedDeliveryDate;
    private String actualDeliveryDate;
    private BigDecimal total;
    private BigDecimal paid;
    private BigDecimal owed;
    private String status;
    private List<OrderDetailResponse> details;
}
