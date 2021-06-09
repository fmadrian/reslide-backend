package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.LoginRequest;
import com.mygroup.backendReslide.dto.request.LogoutRequest;
import com.mygroup.backendReslide.dto.request.RefreshTokenRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.AuthenticationResponse;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.IndividualType;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.model.status.UserRole;
import com.mygroup.backendReslide.repository.IndividualRepository;
import com.mygroup.backendReslide.repository.IndividualTypeRepository;
import com.mygroup.backendReslide.repository.UserRepository;
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

@Service
@AllArgsConstructor
public class AuthService {

    private final IndividualTypeRepository individualTypeRepository;
    private final UserRepository userRepository;
    private final IndividualRepository individualRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    @Transactional
    public void createUser(UserRequest userRequest){
        // Verify that username / code doesn't exist in the database.
        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()){
            throw new UsernameExistsException(userRequest.getUsername()); // Throws an exception if it already exists.
        }if(individualRepository.findByCode(userRequest.getCode()).isPresent()){
            throw new IndividualCodeExistsException(userRequest.getCode()); // Throws an exception if it already exists.
        }

        // Lookup for individual type.
        IndividualType individualType = individualTypeRepository.findByName("PERSON").orElseThrow(()-> {throw new IndividualTypeNotFoundException("PERSON");});
        // Create individual with its details.
        Individual individual = new Individual();
        individual.setCode(userRequest.getCode());
        individual.setName(userRequest.getName());
        individual.setNotes(userRequest.getNotes());
        individual.setStatus(true);
        individual.setType(individualType);
        // Create user with its details.
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // Encodes password.
        user.setCreated(Instant.now());
        user.setRole(UserRole.CASHIER);
        user.setEnabled(true);
        // Link individual and user.
        user.setIndividual(individual);

        // Save BOTH OBJECTS.
        // WE HAVE TO SAVE BOTH OBJECTS, OTHERWISE THE SECOND OBJECT WON'T BE ABLE TO REFERENCE THE FIRST ONE.
        individualRepository.save(individual);
        userRepository.save(user);
    }
    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {

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
        User user = userRepository.findByUsername(principal.getUsername())
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
