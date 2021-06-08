package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.dto.response.ResponseType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenericResponseService {
    public GenericResponse buildError(Exception e){
        return new GenericResponse().builder()
                .responseType(ResponseType.ERROR)
                .errorType(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();
    }
    public GenericResponse buildInformation(String message){
        return new GenericResponse().builder()
                .responseType(ResponseType.INFORMATION)
                .errorType(null)
                .message(message)
                .build();
    }

}
