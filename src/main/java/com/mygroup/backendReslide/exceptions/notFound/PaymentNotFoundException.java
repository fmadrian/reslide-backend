package com.mygroup.backendReslide.exceptions.notFound;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Payment " + id.toString() + " not found");
    }
}