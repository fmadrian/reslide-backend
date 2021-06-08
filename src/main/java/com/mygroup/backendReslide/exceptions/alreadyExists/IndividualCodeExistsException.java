package com.mygroup.backendReslide.exceptions.alreadyExists;

public class IndividualCodeExistsException extends RuntimeException {
    public IndividualCodeExistsException(String code) {
        super("Code " + code + " already exists.");
    }
}
