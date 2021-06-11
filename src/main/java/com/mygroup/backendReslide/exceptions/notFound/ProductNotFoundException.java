package com.mygroup.backendReslide.exceptions.notFound;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String code) {
        super("Product " + code + " not found");
    }

    public ProductNotFoundException(Long id) {
        super("Product " + id.toString() + " not found");
    }
}