package com.mygroup.backendReslide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountDto {
    private Long id;
    private Integer percentage;
    private String reason;
    private String notes;
}
