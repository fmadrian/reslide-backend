package com.mygroup.backendReslide.exceptions.alreadyExists;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(String username) {
        super("Username " + username + " already exists.");
    }
}