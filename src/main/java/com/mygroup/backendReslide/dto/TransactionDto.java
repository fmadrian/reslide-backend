package com.mygroup.backendReslide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private String username;
    private String fullname; // User's full name (used when printing transactions).
    private String date;
    private String notes;
    private List<PaymentDto> payments;
}
