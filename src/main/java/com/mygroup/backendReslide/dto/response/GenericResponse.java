package com.mygroup.backendReslide.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse {
    private ResponseType responseType; // Error, information.
    private String errorType; // Error type.
    private String message; // Response message.
}
