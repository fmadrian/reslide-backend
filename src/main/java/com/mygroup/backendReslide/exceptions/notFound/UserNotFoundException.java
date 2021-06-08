package com.mygroup.backendReslide.exceptions.notFound;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User " + username + " not found");
    }
}