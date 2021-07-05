package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;

public class OrderProductQuantityException extends RuntimeException {
    public OrderProductQuantityException(BigDecimal quantity) {
        super(" Quantity: ("+quantity.toString()+") has to be bigger than 0.");
    }
}

