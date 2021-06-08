package com.mygroup.backendReslide.exceptions.notFound;

public class ProductTypeNotFoundException extends RuntimeException {
    public ProductTypeNotFoundException(String type) {
        super("Product type " + type + " not found");
    }
}
