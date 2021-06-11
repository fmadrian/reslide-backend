package com.mygroup.backendReslide.exceptions.notFound;

public class PaymentMethodNotFoundException extends RuntimeException {
    public PaymentMethodNotFoundException(String type) {
        super("Payment type " + type + " not found");
    }
    public PaymentMethodNotFoundException(Long id) {
        super("Payment type " + id.toString() + " not found");
    }
}

