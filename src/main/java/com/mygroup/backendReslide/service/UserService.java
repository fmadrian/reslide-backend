package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUser_Entity(String username){
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()->new UserNotFoundException(username));
    }
}
