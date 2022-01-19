package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.response.UserResponse;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.mapper.UserMapper;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public User getUser_Entity(String username){
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(()->new UserNotFoundException(username));
    }
    @Transactional(readOnly = true)
    public List<UserResponse> search(String username) {
        List<User> users = this.userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream()
                .map(this.userMapper :: mapToDto)
                .collect(Collectors.toList());

    }
}
