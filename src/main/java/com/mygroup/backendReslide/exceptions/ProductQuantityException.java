package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;

public class ProductQuantityException extends RuntimeException {
    public ProductQuantityException(String code, BigDecimal quantity) {
        super("Product:" + code + " Quantity: "+quantity.toString()+" not available.");
    }
}

