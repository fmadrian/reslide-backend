package com.mygroup.backendReslide.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    // User's name
    private String username;
    // User's password
    private String password;
    // Person's name
    private String name;
    // Person's id
    private String code;
    // Person's notes
    private String notes;

    // TODO: Add contacts and address
    // contacts
    // private List<AddressRequest> addresses;
    // addresses
}
