package com.mygroup.backendReslide.exceptions.alreadyExists;

public class IndividualCodeAlreadyExists extends RuntimeException {
    public IndividualCodeAlreadyExists(String code) {
        super("Code " + code + " already exists.");
    }
}
