package com.mygroup.backendReslide.exceptions.alreadyExists;

public class IndividualTypeExistsException extends RuntimeException {
    public IndividualTypeExistsException(String name) {
        super("Individual type " + name + " already exists.");
    }
}

