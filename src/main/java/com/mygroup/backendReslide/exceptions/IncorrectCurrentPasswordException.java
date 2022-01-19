package com.mygroup.backendReslide.exceptions;

public class IncorrectCurrentPasswordException extends RuntimeException {
    public IncorrectCurrentPasswordException(){
        super("Current password is not correct.");
    }
}