package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.IndividualDto;
import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.dto.request.UserRequest;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualTypeExistsException;
import com.mygroup.backendReslide.exceptions.alreadyExists.UsernameExistsException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class InstallationService {
    private IndividualTypeService individualTypeService;
    private UserService userService;
    public void setup(){
        this.createIndividualType("PERSON");
        this.createIndividualType("BUSINESS");
        this.createAdminUser();
    }

    private void createIndividualType(String name){
        try{
            IndividualTypeDto individualType = new IndividualTypeDto();
            individualType.setName(name);
            this.individualTypeService.create(individualType, true);
        }catch(IndividualTypeExistsException e){
            // Do nothing.
        }catch(Exception e){
            // Throw every other exception.
            throw e;
        }
    }
    private void createAdminUser(){
        try{
            // Sets the user's basic information and creates it.
            UserRequest user = new UserRequest();
            user.setIndividual(new IndividualDto());
            user.setUsername("admin");
            user.setPassword("123456"); // Default password.
            user.getIndividual().setCode("0");
            user.getIndividual().setName("admin");
            user.getIndividual().setType("PERSON");
            user.getIndividual().setContacts(new ArrayList<>());
            user.getIndividual().setAddresses(new ArrayList<>());
            this.userService.createUser(user, true);
        }catch(UsernameExistsException e){
            // Do nothing.
        }
        catch(Exception e){
            throw e;
        }
    }
}
