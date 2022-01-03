package com.mygroup.backendReslide.exceptions;

public class UserNotAuthorizedException extends RuntimeException{
    public UserNotAuthorizedException(String username){
        super("The user " + username + " is not authorized to do this operation.");
    }
}
