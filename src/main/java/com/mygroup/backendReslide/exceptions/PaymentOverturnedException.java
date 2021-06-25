package com.mygroup.backendReslide.exceptions;

public class PaymentOverturnedException extends RuntimeException {
    public PaymentOverturnedException(Long paymentId) {
        super("This payment (" + paymentId +") has already been overturned.");
    }
}
