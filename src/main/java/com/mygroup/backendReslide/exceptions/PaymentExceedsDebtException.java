package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PaymentExceedsDebtException extends RuntimeException {
    public PaymentExceedsDebtException(BigDecimal paid, BigDecimal owed) {
        super("Payment of: "+paid.toString()+" exceeds the debt of "+owed.setScale(2, RoundingMode.HALF_UP));
    }
}
