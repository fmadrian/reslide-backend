package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

// Implements the UserDetailsInterface
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // We search the user.
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

        // Wrapper given by Spring to implement the user's details interface.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),user.isEnabled(), true, true,true,getAuthorities("USER")
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role){
        return singletonList(new SimpleGrantedAuthority(role));
    }
}
