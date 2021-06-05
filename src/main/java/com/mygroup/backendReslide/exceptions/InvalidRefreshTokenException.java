package com.mygroup.backendReslide.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {super("Refresh token not found");}
}

