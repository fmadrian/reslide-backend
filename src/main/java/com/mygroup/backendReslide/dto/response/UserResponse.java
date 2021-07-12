package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.dto.IndividualDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    // User's name
    private String username;
    // Person's name and password
    private IndividualDto individual;
}
