package com.mygroup.backendReslide.exceptions;

public class InvoiceAndDetailDoNotMatchException extends RuntimeException {
    public InvoiceAndDetailDoNotMatchException(Long invoiceDetailId, Long invoiceId) {
        super("Invoice detail ("+ invoiceDetailId.toString()+ ") is not part of invoice (" + invoiceId.toString()+ ").");
    }
}
