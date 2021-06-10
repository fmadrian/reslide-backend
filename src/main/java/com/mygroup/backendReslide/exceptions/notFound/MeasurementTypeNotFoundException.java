package com.mygroup.backendReslide.exceptions.notFound;

public class MeasurementTypeNotFoundException extends RuntimeException {
    public MeasurementTypeNotFoundException(String name) {
        super("Measurement type " + name + " not found");
    }
    public MeasurementTypeNotFoundException(Long id) {
        super("Measurement type " + id.toString() + " not found");
    }
}
