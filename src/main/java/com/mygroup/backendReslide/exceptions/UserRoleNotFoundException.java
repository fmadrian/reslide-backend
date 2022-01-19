package com.mygroup.backendReslide.exceptions;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String role) {
        super ("User role " + role + " not found.");
    }
}
