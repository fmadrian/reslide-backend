package com.mygroup.backendReslide.model.status;

public enum UserRole {
    ADMIN("admin"),
    CASHIER("cashier");
    private String role;
    UserRole(String role){
        this.role = role;
    }
}
