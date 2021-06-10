package com.mygroup.backendReslide.exceptions.alreadyExists;

public class MeasurementTypeExistsException extends RuntimeException {
    public MeasurementTypeExistsException(String name) {
        super("Measurement type " + name + " already exists.");
    }
}
