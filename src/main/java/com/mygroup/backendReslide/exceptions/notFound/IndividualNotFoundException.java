package com.mygroup.backendReslide.exceptions.notFound;

public class IndividualNotFoundException extends RuntimeException {
    public IndividualNotFoundException(Long id) {
        super("Individual " + id + " not found");
    }
    public IndividualNotFoundException(String code) {
        super("Individual " + code + " not found");
    }
}
