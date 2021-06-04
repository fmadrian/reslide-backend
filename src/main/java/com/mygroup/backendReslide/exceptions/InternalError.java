package com.mygroup.backendReslide.exceptions;

public class InternalError extends RuntimeException {
    public InternalError(Exception e) {
        super("Internal server error.");
    }
}
