package com.mygroup.backendReslide.dto.response;

public enum ResponseType {
    ERROR("ERROR"),
    INFORMATION("INFORMATION");

    private String type;
    ResponseType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
