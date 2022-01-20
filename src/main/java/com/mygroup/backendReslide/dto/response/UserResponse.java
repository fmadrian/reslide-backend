package com.mygroup.backendReslide.dto.response;

import com.mygroup.backendReslide.dto.IndividualDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    // User's name
    private String username;
    private String role;
    private boolean enabled;
    // Person's information.
    private IndividualDto individual;
}
