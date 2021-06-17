package com.mygroup.backendReslide.model.status;

import java.util.Locale;

public enum InvoiceDetailStatus {
    ACTIVE("active"),
    RETURNED("returned"),
    DELETED("deleted");

    private String status;
    InvoiceDetailStatus(String status){
        this.status = status;
    }

    public String getStatus(){return status;}

}
