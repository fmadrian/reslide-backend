package com.mygroup.backendReslide.model.status;

public enum OrderStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");
    OrderStatus(String status){
        this.status = status;
    }
    private String status;
}
