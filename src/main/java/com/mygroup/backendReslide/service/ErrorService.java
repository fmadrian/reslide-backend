package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.response.ErrorResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ErrorService {
    public ErrorResponse buildError(Exception e){
        ErrorResponse response = new ErrorResponse();
        response.setType(e.getClass().getSimpleName());
        response.setMessage(e.getMessage());
        return response;
    }
}
