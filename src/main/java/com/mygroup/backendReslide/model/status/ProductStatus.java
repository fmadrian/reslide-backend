package com.mygroup.backendReslide.model.status;

public enum ProductStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DISCONTINUED("discontinued");
    ProductStatus(String status){
        this.status = status;
    }
    private String status;
}
