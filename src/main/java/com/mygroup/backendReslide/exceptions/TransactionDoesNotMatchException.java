package com.mygroup.backendReslide.exceptions;

public class TransactionDoesNotMatchException extends RuntimeException {
    public TransactionDoesNotMatchException(Long transactionId) {
        super("This transaction id (" + transactionId.toString() + ") doesn't match with the invoice / order.");
    }
}
