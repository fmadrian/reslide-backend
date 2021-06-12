package com.mygroup.backendReslide.dto;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualDto {
    private Long id;
    private String type;
    private String code;
    private String name;
    private List<ContactDto> contacts;
    private List<AddressDto> addresses;
    private String notes;
    private boolean enabled;
}
