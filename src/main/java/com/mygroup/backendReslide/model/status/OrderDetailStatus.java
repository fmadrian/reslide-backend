package com.mygroup.backendReslide.model.status;

public enum OrderDetailStatus {
    DELIVERED("delivered"),
    NOT_DELIVERED("not_delivered"),
    RETURNED("returned"),
    CANCELED("canceled");
    OrderDetailStatus(String status){
        this.status = status;
    }
    private String status;
}
