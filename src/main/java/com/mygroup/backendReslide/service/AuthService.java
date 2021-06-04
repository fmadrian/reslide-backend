package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeAlreadyExists;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameAlreadyExists;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.IndividualType;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.model.status.DatabaseStatus;
import com.mygroup.backendReslide.model.status.UserRole;
import com.mygroup.backendReslide.model.status.UserStatus;
import com.mygroup.backendReslide.repository.IndividualRepository;
import com.mygroup.backendReslide.repository.IndividualTypeRepository;
import com.mygroup.backendReslide.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
@AllArgsConstructor
public class AuthService {

    private final IndividualTypeRepository individualTypeRepository;
    private final UserRepository userRepository;
    private final IndividualRepository individualRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void createUser(UserRequest userRequest){
        // Verify that username / code doesn't exist in the database.
        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()){
            throw new UsernameAlreadyExists(userRequest.getUsername()); // Throws an exception if it already exists.
        }if(individualRepository.findByCode(userRequest.getCode()).isPresent()){
            throw new IndividualCodeAlreadyExists(userRequest.getCode()); // Throws an exception if it already exists.
        }

        // Lookup for individual type.
        IndividualType individualType = individualTypeRepository.findByName("PERSON").orElseThrow(()-> {throw new IndividualTypeNotFoundException("PERSON");});
        // Create individual with its details.
        Individual individual = new Individual();
        individual.setCode(userRequest.getCode());
        individual.setName(userRequest.getName());
        individual.setNotes(userRequest.getNotes());
        individual.setStatus(DatabaseStatus.ACTIVE);
        individual.setType(individualType);
        // Create user with its details.
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // Encodes password.
        user.setCreated(Instant.now());
        user.setRole(UserRole.CASHIER);
        user.setStatus(UserStatus.ACTIVE);
        // Link individual and user.
        user.setIndividual(individual);

        // Save BOTH OBJECTS.
        // WE HAVE TO SAVE BOTH OBJECTS, OTHERWISE THE SECOND OBJECT WON'T BE ABLE TO REFERENCE THE FIRST ONE.
        individualRepository.save(individual);
        userRepository.save(user);
    }

}
