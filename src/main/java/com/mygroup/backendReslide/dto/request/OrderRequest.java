package com.mygroup.backendReslide.dto.request;

import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long id;
    private TransactionDto transaction;
    private String providerCode;
    private String date;
    private String expectedDeliveryDate;
    private String status;
    private List<OrderDetailRequest> details;
}
