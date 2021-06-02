package com.mygroup.backendReslide.model.status;

public enum InvoiceStatus {
    ACTIVE("active"),
    PENDING("with_problems"),
    RETURNED("returned"),
    DELETED("deleted");
    InvoiceStatus(String status){
        this.status = status;
    }
    private String status;
}
