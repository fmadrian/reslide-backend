package com.mygroup.backendReslide.exceptions.notFound;

public class OrderDetailNotFoundException extends RuntimeException {
    public OrderDetailNotFoundException(Long id) {
        super("Order detail " + id + " not found");
    }
}