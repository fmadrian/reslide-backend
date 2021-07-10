package com.mygroup.backendReslide.dto.request;

import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequest {
    private Long id;
    private String productCode;
    private Long orderId;
    private BigDecimal quantity;
    private BigDecimal priceByUnit;
    private String status;
    private String notes;
}
