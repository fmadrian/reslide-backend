package com.mygroup.backendReslide.exceptions.notFound;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(Long id) {
        super("Address " + id.toString() + " not found");
    }
}
