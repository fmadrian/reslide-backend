package com.mygroup.backendReslide.exceptions;

public class JwtSecurityException extends RuntimeException {
    public JwtSecurityException(Exception e) {super(e);}
    public JwtSecurityException(String message, Exception e) {super(message, e);}
}
