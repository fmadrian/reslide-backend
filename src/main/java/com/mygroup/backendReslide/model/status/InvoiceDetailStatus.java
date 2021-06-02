package com.mygroup.backendReslide.model.status;

public enum InvoiceDetailStatus {
    ACTIVE("active"),
    RETURNED("returned"),
    DELETED("deleted");
    InvoiceDetailStatus(String status){
        this.status = status;
    }
    private String status;
}
