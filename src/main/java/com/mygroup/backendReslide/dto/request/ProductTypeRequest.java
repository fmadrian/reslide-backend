package com.mygroup.backendReslide.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTypeRequest {
    private Long id;
    private String type;
    private String notes;
}
