package com.mygroup.backendReslide.exceptions;

public class OrderAndDetailDoNotMatchException extends RuntimeException {
    public OrderAndDetailDoNotMatchException(Long detailId, Long orderId) {
        super("Order detail ("+ detailId.toString()+ ") is not part of order (" + orderId.toString()+ ").");
    }
}

