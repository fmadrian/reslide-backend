package com.mygroup.backendReslide.model.status;

import java.util.Locale;

public enum InvoiceStatus {
    DELIVERED("delivered"),
    NOT_DELIVERED("not_delivered"),
    DELETED("deleted");

    private String status;
    InvoiceStatus(String status){
        this.status = status;
    }

    public String getStatus(){return this.status;}
    public InvoiceStatus getInvoiceStatus(String status){return InvoiceStatus.valueOf(status.toUpperCase(Locale.ROOT));}

}
