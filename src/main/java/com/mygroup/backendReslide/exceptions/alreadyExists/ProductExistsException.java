package com.mygroup.backendReslide.exceptions.alreadyExists;

public class ProductExistsException extends RuntimeException {
    public ProductExistsException(String code) {
        super("Product " + code + " already exists.");
    }
}
