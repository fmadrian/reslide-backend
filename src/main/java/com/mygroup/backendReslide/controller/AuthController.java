package com.mygroup.backendReslide.controller;

import com.mygroup.backendReslide.dto.request.LoginRequest;
import com.mygroup.backendReslide.dto.request.LogoutRequest;
import com.mygroup.backendReslide.dto.request.RefreshTokenRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.AuthenticationResponse;
import com.mygroup.backendReslide.dto.response.GenericResponse;
import com.mygroup.backendReslide.exceptions.InternalError;
import com.mygroup.backendReslide.exceptions.InvalidRefreshTokenException;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.service.AuthService;
import com.mygroup.backendReslide.service.GenericResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController // REST
@RequestMapping("/api/auth") // URL
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GenericResponseService responseService;


    @PostMapping("/create/user")
    public ResponseEntity createUser(@RequestBody UserRequest userRequest){
        try {
            authService.createUser(userRequest);
            return new ResponseEntity(responseService.buildInformation("User created successfully."), HttpStatus.OK);
        }catch (UsernameExistsException | UserNotAuthorizedException | IndividualCodeExistsException | IndividualTypeNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.CONFLICT);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update/user")
    public ResponseEntity updateUser(@RequestBody UserRequest userRequest){
        try {
            authService.updateUser(userRequest);
            return new ResponseEntity(responseService.buildInformation("Updated."), HttpStatus.OK);
        }catch (UsernameNotFoundException  | UserNotAuthorizedException | UsernameExistsException | IndividualCodeExistsException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get/user")
    public ResponseEntity getUserInformation(){
        try{
            return new ResponseEntity(authService.getUserInformation(), HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        try {
            AuthenticationResponse response = authService.login(loginRequest);
            return new ResponseEntity<AuthenticationResponse>(response, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/refresh/token")
    public ResponseEntity refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        try {
            AuthenticationResponse response = authService.refreshToken(refreshTokenRequest);
            return new ResponseEntity<AuthenticationResponse>(response, HttpStatus.OK);
        }catch (InvalidRefreshTokenException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/logout")
    public ResponseEntity logout(@Valid @RequestBody LogoutRequest logoutRequest){
        try{
            authService.logout(logoutRequest);
            return new ResponseEntity(responseService.buildInformation("User logged out."), HttpStatus.OK);
        }catch (InvalidRefreshTokenException e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(e), HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            return new ResponseEntity<GenericResponse>(responseService.buildError(new InternalError(e)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
