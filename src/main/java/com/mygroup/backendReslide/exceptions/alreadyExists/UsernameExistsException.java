package com.mygroup.backendReslide.exceptions.alreadyExists;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String username) {
        super("Username " + username + " already exists.");
    }
}