package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.LoginRequest;
import com.mygroup.backendReslide.dto.request.LogoutRequest;
import com.mygroup.backendReslide.dto.request.RefreshTokenRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.AuthenticationResponse;
import com.mygroup.backendReslide.dto.response.UserResponse;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.mapper.UserMapper;
import com.mygroup.backendReslide.model.*;
import com.mygroup.backendReslide.model.status.UserRole;
import com.mygroup.backendReslide.repository.*;
import com.mygroup.backendReslide.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {
        // Makes sure that the user exists and is enabled.
        userRepository.findByUsernameIgnoreCaseAndEnabled(loginRequest.getUsername(), true)
                .orElseThrow(()-> new UserNotFoundException(loginRequest.getUsername()));

        // Create a username + password token
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        // Load that token into the context of the security context holder.

        // If we want to know if a determined user is logged in or not, we have to check the security context.
        // for the authenticate object. If we can find the object, the user is logged in.
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        // We generate and return the JWT Token to the user.
        String jwtToken = jwtProvider.generateToken(authenticate);
        // To send the token we'll use the DTO AuthenticationResponse.
        return new AuthenticationResponse().builder()
                .authenticationToken(jwtToken)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis())) // now + expiration time
                .username(loginRequest.getUsername())
                .build();

    }
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        // 1. Validate the incoming refresh token.
        refreshTokenService.validateRefreshToken(refreshToken);
        // 2. Generate a new JW Token.
        String jwToken = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
        // 3. We build and return the response using the same refresh token.
        return new AuthenticationResponse().builder()
                .refreshToken(refreshToken)
                .authenticationToken(jwToken)
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
    @Transactional(readOnly = true)
    public User getCurrentUser(){
        // 1. Get the User name from the security context holder.
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 2. Search the user in the database using the retrieved name.
        // If it doesn't find it, throws an exception.
        User user = userRepository.findByUsernameIgnoreCase(principal.getUsername())
                .orElseThrow(()-> new UserNotFoundException(principal.getUsername()));

        return user;
    }
    public boolean isLoggedIn(){
        // Get the authentication object from the security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Returns whether there is an authenticated (not anonymous) authentication token.
        return !(authentication instanceof AnonymousAuthenticationToken) &&authentication.isAuthenticated();
    }
    @Transactional
    public void logout(LogoutRequest logoutRequest){
        // Validate and remove the authentication token from the database.
        refreshTokenService.validateRefreshToken(logoutRequest.getRefreshToken());
        refreshTokenService.deleteRefreshToken(logoutRequest.getRefreshToken());
    }
}
