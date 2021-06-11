package com.mygroup.backendReslide.exceptions.alreadyExists;

public class ContactTypeExistsException extends RuntimeException {
    public ContactTypeExistsException(String type) {
        super("Code " + type + " already exists.");
    }
}
