package com.mygroup.backendReslide.exceptions.notFound;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super("Invoice " + id + " not found");
    }
    public InvoiceNotFoundException(String code) {
        super("Invoice " + code + " not found");
    }
}
