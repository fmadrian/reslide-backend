package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;

public class PriceNotValidException extends RuntimeException {
    public PriceNotValidException(BigDecimal number) {
        super("Price must be bigger than 0. Attempted amount: (" + number.toString() + ").");
    }
}