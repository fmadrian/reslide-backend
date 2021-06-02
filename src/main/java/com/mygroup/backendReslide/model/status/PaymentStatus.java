package com.mygroup.backendReslide.model.status;

public enum PaymentStatus {
    PAID("paid"),
    NOT_PAID("not_paid"),
    OVERTURNED("overturned");
    PaymentStatus(String status){
        this.status = status;
    }
    private String status;
}
