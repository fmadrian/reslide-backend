package com.mygroup.backendReslide.exceptions;

public class DiscountNotValidException extends RuntimeException {
    public DiscountNotValidException(Integer discount) {
        super("Discount " + discount.toString() +" is not valid.");
    }
}
