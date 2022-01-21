package com.mygroup.backendReslide.exceptions.notFound;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(){
        super("Not allowed to do this.");
    }
}
