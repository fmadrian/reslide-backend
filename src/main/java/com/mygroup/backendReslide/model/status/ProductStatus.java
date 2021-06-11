package com.mygroup.backendReslide.model.status;

public enum ProductStatus {

    ACTIVE("active"),
    INACTIVE("inactive"),
    DISCONTINUED("discontinued");

    private String status;

    ProductStatus(String status){
        this.status = status;
    }

    public String getStatus(){return this.status;}


}
