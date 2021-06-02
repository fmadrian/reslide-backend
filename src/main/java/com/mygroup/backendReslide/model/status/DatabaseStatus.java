package com.mygroup.backendReslide.model.status;

public enum DatabaseStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DELETED("deleted");
    DatabaseStatus(String status){
        this.status = status;
    }
    private String status;
}
