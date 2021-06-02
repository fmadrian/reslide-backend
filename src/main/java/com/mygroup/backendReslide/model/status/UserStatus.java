package com.mygroup.backendReslide.model.status;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive");
    UserStatus(String status){
        this.status = status;
    }
    private String status;

}
