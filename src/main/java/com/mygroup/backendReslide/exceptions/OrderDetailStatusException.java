package com.mygroup.backendReslide.exceptions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderDetailStatusException extends RuntimeException {
    public OrderDetailStatusException(Long id) {
        super("You can't change this ("+ id.toString() +") status");
    }
}
