package com.mygroup.backendReslide.exceptions.notFound;

public class ContactNotFoundException extends RuntimeException {
    public ContactNotFoundException(Long id) {
        super("Contact " + id.toString() + " not found");
    }
}
