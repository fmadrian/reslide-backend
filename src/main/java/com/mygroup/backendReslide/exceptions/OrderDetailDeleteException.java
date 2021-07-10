package com.mygroup.backendReslide.exceptions;

public class OrderDetailDeleteException extends RuntimeException {
    public OrderDetailDeleteException(Long id) {
        super("You can't delete this ("+ id.toString() +") detail because it's been delivered.");
    }
}