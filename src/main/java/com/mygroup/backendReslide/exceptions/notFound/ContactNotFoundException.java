package com.mygroup.backendReslide.exceptions.notFound;

public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(String type) {
        super("Contact type " + type + " not found");
    }
    public ContactNotFoundException(Long id) {
        super("Contact type " + id.toString() + " not found");
    }
}
