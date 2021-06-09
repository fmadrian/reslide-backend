package com.mygroup.backendReslide.exceptions.notFound;

public class ProductBrandNotFoundException extends RuntimeException {
    public ProductBrandNotFoundException(String type) {
        super("Product brand " + type + " not found");
    }

    public ProductBrandNotFoundException(Long id) {
        super("Product brand " + id.toString() + " not found");
    }
}