package com.mygroup.backendReslide.exceptions.notFound;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction " + id.toString() + " not found");
    }
}
