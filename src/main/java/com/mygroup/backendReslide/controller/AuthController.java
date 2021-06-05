package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.LoginRequest;
import com.mygroup.backendReslide.dto.request.RefreshTokenRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.AuthenticationResponse;
import com.mygroup.backendReslide.dto.response.ErrorResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.InvalidRefreshTokenException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeAlreadyExists;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameAlreadyExists;
import com.mygroup.backendReslide.service.AuthService;
import com.mygroup.backendReslide.service.ErrorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
            return new ResponseEntity<ErrorResponse>(errorService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<ErrorResponse>(errorService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        try {
            AuthenticationResponse response = authService.login(loginRequest);
            return new ResponseEntity<AuthenticationResponse>(response, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<ErrorResponse>(errorService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/refresh/token")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        try {
            AuthenticationResponse response = authService.refreshToken(refreshTokenRequest);
            return new ResponseEntity<AuthenticationResponse>(response, HttpStatus.OK);
        }catch (InvalidRefreshTokenException e){
            return new ResponseEntity<ErrorResponse>(errorService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<ErrorResponse>(errorService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
