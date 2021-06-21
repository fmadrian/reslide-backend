package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;

public class PaymentQuantityException extends RuntimeException {
    public PaymentQuantityException(BigDecimal paid) {
        super("Payment must be bigger than 0. Attempted amount: (" + paid.toString() + ").");
    }
}
