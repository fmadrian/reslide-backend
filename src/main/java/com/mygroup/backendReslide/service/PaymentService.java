package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentService {


    @Transactional
    public List<Payment> validatePayments(Invoice invoice){
        List<Payment> payments = invoice.getTransaction().getPayments();
        return payments.stream()
                .map(
                        (payment) -> {
                            // Validate amount (Paid now + paid before <= total debt)
                            if(payment.getPaid().add(invoice.getPaid()).compareTo(invoice.getOwed()) == 1){
                                throw new PaymentExceedsDebtException(payment.getPaid(), invoice.getOwed());
                            }
                            if(payment.getPaid().compareTo(BigDecimal.ZERO) != 1){
                                throw new PaymentQuantityException(payment.getPaid());
                            }
                            // Adjust the owed value in the invoice.
                            // The invoice will be changed in the invoice create method.
                            payment.setOwedBefore(invoice.getOwed());
                            // Change made in the invoice, just as a reference for the next payment line (if there is any).
                            invoice.setOwed(invoice.getOwed().subtract(payment.getPaid()));
                            payment.setOwedAfter(invoice.getOwed());
                            // Set the status.
                            payment.setStatus(PaymentStatus.ACTIVE);
                            return payment;
                        }
                ).collect(Collectors.toList());
    }
}
