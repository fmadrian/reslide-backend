package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private String productCode;
    private String productName;
    private BigDecimal priceByUnit;
    private BigDecimal quantity;
    private BigDecimal total;
    private String status;
    private String notes;
}
