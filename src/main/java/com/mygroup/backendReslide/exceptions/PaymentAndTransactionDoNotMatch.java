package com.mygroup.backendReslide.exceptions;

public class PaymentAndTransactionDoNotMatch extends RuntimeException {
    public PaymentAndTransactionDoNotMatch(Long paymentId, Long transactionId) {
        super("Payment ("+ paymentId.toString()+ ") is not part of transaction (" + transactionId.toString()+ ").");
    }
}
