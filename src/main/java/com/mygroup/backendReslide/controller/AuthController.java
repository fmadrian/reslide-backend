package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.ErrorResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeAlreadyExists;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameAlreadyExists;
import com.mygroup.backendReslide.service.AuthService;
import com.mygroup.backendReslide.service.ErrorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST
@RequestMapping("/api/auth") // URL
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ErrorService errorService;

    @PostMapping("/create/user")
    public ResponseEntity createUser(@RequestBody UserRequest userRequest){
        try {
            authService.createUser(userRequest);
            return new ResponseEntity("User created successfully", HttpStatus.OK);
        }catch (UsernameAlreadyExists | IndividualCodeAlreadyExists e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
