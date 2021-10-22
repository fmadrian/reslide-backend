package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;

public class QuantityNotValidException extends RuntimeException {
    public QuantityNotValidException(BigDecimal number) {
        super("Quantity must be bigger than 0. Attempted amount: (" + number.toString() + ").");
    }
}
