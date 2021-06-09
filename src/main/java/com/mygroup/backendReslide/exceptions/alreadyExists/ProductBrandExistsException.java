package com.mygroup.backendReslide.exceptions.alreadyExists;

public class ProductBrandExistsException extends RuntimeException {
    public ProductBrandExistsException(String type) {
        super("Product brand " + type + " already exists.");
    }
}
