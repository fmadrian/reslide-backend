package com.mygroup.backendReslide.exceptions.notFound;

public class ContactTypeNotFoundException extends RuntimeException {
    public ContactTypeNotFoundException(String type) {
        super("Contact type " + type + " not found");
    }
    public ContactTypeNotFoundException(Long id) {
        super("Contact type " + id.toString() + " not found");
    }
}
