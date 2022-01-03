package com.mygroup.backendReslide.exceptions.notFound;

public class IndividualTypeNotFoundException extends RuntimeException {
    public IndividualTypeNotFoundException(String individualType) {
        super("Individual type " + individualType + " not found");
    }
    public IndividualTypeNotFoundException(Long id) {
        super("Individual type " + id + " not found");
    }
}
