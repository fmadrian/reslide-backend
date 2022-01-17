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
    private final IndividualRepository individualRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    @Transactional
    public void createUser(UserRequest userRequest){
        // Only administrators can do this operation.
        if(!this.getCurrentUser().getRole().equals(UserRole.ADMIN)){
            throw new UserNotAuthorizedException(this.getCurrentUser().getUsername());
        }

        // Verify that username / code doesn't exist in the database.
        if(userRepository.findByUsernameIgnoreCase(userRequest.getUsername()).isPresent()){
            throw new UsernameExistsException(userRequest.getUsername());
        }if(individualRepository.findByCodeIgnoreCase(userRequest.getIndividual().getCode()).isPresent()){
            throw new IndividualCodeExistsException(userRequest.getIndividual().getCode());
        }
        // Create user from mapped object and some changes.
        User user = userMapper.mapToEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // Encodes password.
        user.setCreated(Instant.now());
        user.setRole(UserRole.CASHIER);
        user.setEnabled(true);
        List<Contact> contacts = user.getIndividual().getContacts();
        List<Address> addresses = user.getIndividual().getAddresses();
        // Save the contacts and the addresses, then save the individual.
        if(!contacts.isEmpty())
            contactRepository.saveAll(contacts);
        if(!addresses.isEmpty())
            addressRepository.saveAll(addresses);
        // Save BOTH OBJECTS.
        // WE HAVE TO SAVE BOTH OBJECTS, OTHERWISE THE SECOND OBJECT WON'T BE ABLE TO REFERENCE THE FIRST ONE.
        individualRepository.save(user.getIndividual());
        userRepository.save(user);
    }
    @Transactional
    public void updateUser(UserRequest userRequest){
        User updatedUser = userMapper.mapToEntity(userRequest);
        // Searches the user
        User user = userRepository.findByUsernameIgnoreCase(userRequest.getUsername())
                .orElseThrow(()->new UserNotFoundException(userRequest.getUsername()));
        // Verifies that the updated username / code doesn't exist in the database.
        if(userRepository.findByUsernameIgnoreCase(userRequest.getUsername()).isPresent()
        && !user.getUsername().equals(userRequest.getUsername())){
            throw new UsernameExistsException(userRequest.getUsername());
        }if(individualRepository.findByCodeIgnoreCase(userRequest.getIndividual().getCode()).isPresent()
        && !user.getIndividual().getCode().equals(userRequest.getIndividual().getCode())){
            throw new IndividualCodeExistsException(userRequest.getIndividual().getCode());
        }
        // Do the modifications.
        user.setUsername(updatedUser.getUsername());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Encodes password.

        user.getIndividual().setName(updatedUser.getIndividual().getName());
        user.getIndividual().setCode(updatedUser.getIndividual().getCode());
        user.getIndividual().setAddresses(updatedUser.getIndividual().getAddresses());
        user.getIndividual().setContacts(updatedUser.getIndividual().getContacts());
        user.getIndividual().setNotes(updatedUser.getIndividual().getNotes());

        List<Contact> contacts = user.getIndividual().getContacts();
        List<Address> addresses = user.getIndividual().getAddresses();
        // Save the contacts and the addresses, then save the individual.
        if(!contacts.isEmpty())
            contactRepository.saveAll(contacts);
        if(!addresses.isEmpty())
            addressRepository.saveAll(addresses);
        individualRepository.save(user.getIndividual());
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
        User user = userRepository.findByUsernameIgnoreCase(principal.getUsername())
                .orElseThrow(()-> new UserNotFoundException(principal.getUsername()));

        return user;
    }
    @Transactional(readOnly = true)
    public UserResponse getUserInformation(){
        return userMapper.mapToDto(this.getCurrentUser());
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
