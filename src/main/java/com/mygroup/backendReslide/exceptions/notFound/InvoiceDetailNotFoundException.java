package com.mygroup.backendReslide.exceptions.notFound;

public class InvoiceDetailNotFoundException extends RuntimeException {
    public InvoiceDetailNotFoundException(Long id) {
        super("Invoice detail " + id + " not found");
    }
}

