package com.mygroup.backendReslide.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualTypeDto {
    private Long id;
    private String name;
    private boolean enabled;
    private String notes;
}
