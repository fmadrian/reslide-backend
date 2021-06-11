package com.mygroup.backendReslide.exceptions.alreadyExists;

public class PaymentMethodExistsException extends RuntimeException {
    public PaymentMethodExistsException(String type) {
        super("Payment type " + type + " already exists.");
    }
}
