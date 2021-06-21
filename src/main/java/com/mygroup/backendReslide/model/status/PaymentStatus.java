package com.mygroup.backendReslide.model.status;

public enum PaymentStatus {
    ACTIVE("active"),
    DELETED("deleted"),
    OVERTURNED("overturned");
    PaymentStatus(String status){
        this.status = status;
    }
    private String status;
}
