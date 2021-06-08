package com.mygroup.backendReslide.exceptions.alreadyExists;

public class ProductTypeExistsException extends RuntimeException {
    public ProductTypeExistsException(String type) {
        super("Product type " + type + " already exists.");
    }
}
