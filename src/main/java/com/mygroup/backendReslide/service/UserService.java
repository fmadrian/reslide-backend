package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.UpdateUserRequest;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.dto.response.UserResponse;
import com.mygroup.backendReslide.exceptions.IncorrectCurrentPasswordException;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.UserRoleNotFoundException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.mapper.UserMapper;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.model.Contact;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.model.status.InvoiceStatus;
import com.mygroup.backendReslide.model.status.UserRole;
import com.mygroup.backendReslide.repository.AddressRepository;
import com.mygroup.backendReslide.repository.ContactRepository;
import com.mygroup.backendReslide.repository.IndividualRepository;
import com.mygroup.backendReslide.repository.UserRepository;
import com.mygroup.backendReslide.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final AuthService authService;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final IndividualRepository individualRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserRequest userRequest){
        User currentUser = this.authService.getCurrentUser();
        // Only administrators can do this operation.
        if(!currentUser.getRole().equals(UserRole.ADMIN)){
            throw new UserNotAuthorizedException(currentUser.getUsername());
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

    // Updates the user that is logged in.
    @Transactional
    public void updateCurrentUser(UpdateUserRequest userRequest){
        // Gets the user
        User user = this.authService.getCurrentUser();
        // Make sure that the user's current password is correct.
        if(!this.passwordEncoder.matches(userRequest.getCurrentPassword(), user.getPassword())){
            throw new IncorrectCurrentPasswordException();
        }
        this.changeUserInformation(user,userRequest);
    }
    // Updates any user (gets username from parameter in path). User who does the call has to be authorized
    // Gets the new username from the user request
    @Transactional
    public void updateUser(String username, UpdateUserRequest userRequest){
        isUserAuthorized();
        // Searches the user
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()->new UserNotFoundException(username));
        // Changes the username
        this.changeUserInformation(user,userRequest);
    }

    // Takes a user object and a user request, Makes changes defined in the user object into the existent user.
    @Transactional
    private void changeUserInformation(User user, UpdateUserRequest userRequest){
        User updatedUser = userMapper.mapToEntity(userRequest);
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
    public void switchStatus(UserRequest userRequest){
        isUserAuthorized();
        // Searches the user
        User user = userRepository.findByUsernameIgnoreCase(userRequest.getUsername())
                .orElseThrow(()->new UserNotFoundException(userRequest.getUsername()));
        user.setEnabled(!user.isEnabled());
        this.userRepository.save(user);
    }
    @Transactional
    public void switchRole(UserRequest userRequest){
        isUserAuthorized();
        // Searches the user
        User user = userRepository.findByUsernameIgnoreCase(userRequest.getUsername())
                .orElseThrow(()->new UserNotFoundException(userRequest.getUsername()));

        if(user.getRole().equals(UserRole.ADMIN)) {
            user.setRole(UserRole.CASHIER);
        }else{
            user.setRole(UserRole.ADMIN);
        }
        this.userRepository.save(user);
    }
    // Throws an exception if the user is not an administrator.
    @Transactional(readOnly = true)
    private void isUserAuthorized(){
        // User must be admin to see other users information.
        User currentUser =this.authService.getCurrentUser();
        if(!currentUser.getRole().equals(UserRole.ADMIN)){
            throw new UserNotAuthorizedException(currentUser.getUsername());
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInformation(){
        return userMapper.mapToDto(this.authService.getCurrentUser());
    }
    @Transactional(readOnly = true)
    public User getUser_Entity(String username){
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()->new UserNotFoundException(username));
    }
    @Transactional(readOnly = true)
    public List<UserResponse> search(String username) {
        isUserAuthorized();
        List<User> users = this.userRepository.findByUsernameContainingIgnoreCaseAndEnabled(username, true);
        return users.stream()
                .map(this.userMapper :: mapToDto)
                .collect(Collectors.toList());

    }
    @Transactional(readOnly = true)
    public UserResponse getUser(String username) {
        isUserAuthorized();
        return this.userMapper
                .mapToDto(userRepository.findByUsernameIgnoreCase(username)
                        .orElseThrow(()->new UserNotFoundException(username)));
    }

}
