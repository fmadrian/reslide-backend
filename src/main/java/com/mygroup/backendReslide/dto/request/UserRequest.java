package com.mygroup.backendReslide.dto.request;

import com.mygroup.backendReslide.dto.IndividualDto;
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
    // Person's notes
    private String notes;
    // Person's information
    private IndividualDto individual;
}
