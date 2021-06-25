package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.exceptions.*;
import com.mygroup.backendReslide.exceptions.notFound.PaymentNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.TransactionNotFoundException;
import com.mygroup.backendReslide.mapper.PaymentMapper;
import com.mygroup.backendReslide.mapper.TransactionMapper;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import com.mygroup.backendReslide.repository.InvoiceRepository;
import com.mygroup.backendReslide.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    @Transactional
    public void create(PaymentDto paymentRequest){
        Long transactionId = paymentRequest.getTransactionId();
        Payment payment = paymentMapper.mapToEntity(paymentRequest);
        try{
            // Search invoice
            Invoice invoice = getInvoice(transactionId);
            // Maps the new payments and validates them.
            payment = validateInvoicePayment(payment, invoice);
            // Store payments and recalculate the invoice.
            paymentRepository.save(payment);
            invoice.getTransaction().getPayments().add(payment);
            recalculateInvoice(invoice);
        }catch (TransactionNotFoundException e){
            // TODO: ORDERS
            throw e;
        }
    }

    @Transactional
    public void update(PaymentDto paymentRequest){
        Long transactionId = paymentRequest.getTransactionId();
        Payment newPayment = paymentMapper.mapToEntity(paymentRequest);
        Payment oldPayment = paymentRepository.findById(paymentRequest.getId())
                .orElseThrow(() -> new PaymentNotFoundException(paymentRequest.getId()));
        try {
            // Search invoice
            Invoice invoice = getInvoice(transactionId);
            // Rollback to the previous debt before this particular payment.
            invoice.setPaid(invoice.getPaid().subtract(oldPayment.getPaid())); // paid (invoice) - payment
            invoice.setOwed(invoice.getTotal().subtract(invoice.getPaid()));
            // Change how much is owed before doing the validation.
            oldPayment.setPaid(newPayment.getPaid());
            // Validates the payment.
            oldPayment = validateInvoicePayment(oldPayment, invoice);
            // Change the remaining values.
            oldPayment.setPaymentMethod(newPayment.getPaymentMethod());
            oldPayment.setDate(newPayment.getDate());
            oldPayment.setNotes(newPayment.getNotes());
            paymentRepository.save(oldPayment);
            recalculateInvoice(invoice);
        }
        catch (TransactionNotFoundException e){
            // TODO: ORDERS
            throw e;
        }
    }

    @Transactional
    public void overturn(PaymentDto paymentRequest) {
        Long transactionId = paymentRequest.getTransactionId();
        Payment payment = paymentMapper.mapToEntity(paymentRequest);

        try{
            if(!payment.getStatus().equals(PaymentStatus.ACTIVE)){
                throw new PaymentOverturnedException(payment.getId());
            }
            // Search invoice
            Invoice invoice = getInvoice(transactionId);
            // Subtract the previous payment of the invoice.
            invoice.setPaid(invoice.getPaid().subtract(payment.getPaid()));
            invoice.setOwed(invoice.getTotal().subtract(invoice.getPaid()));
            // Change the status of the payment
            payment.setStatus(PaymentStatus.OVERTURNED);
            // Store the changes.
            paymentRepository.save(payment);
            invoiceRepository.save(invoice);


        }catch (TransactionNotFoundException e){
            // TODO: ORDERS
            throw e;
        }
    }

    @Transactional
    private void recalculateInvoice(Invoice invoice){
        List<Payment> payments = invoice.getTransaction().getPayments();
        BigDecimal paid = payments.stream()
                .map(payment -> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds the values to the invoice.
        invoice.setPaid(paid);
        invoice.setOwed(invoice.getTotal().subtract(paid)); // total - paid
        invoiceRepository.save(invoice);
    }

    @Transactional
    public List<Payment> validatePayments(Invoice invoice){
        List<Payment> payments = invoice.getTransaction().getPayments();
        return payments.stream()
                .map(payment -> validateInvoicePayment(payment, invoice))
                .collect(Collectors.toList());
    }
    @Transactional
    public Payment validateInvoicePayment(Payment payment, Invoice invoice){
        // payment.setUser(authService.getCurrentUser()); // TODO: Remove the commentary.
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

    private Invoice getInvoice(Long transactionId){
        // Search invoice
        Invoice invoice = invoiceRepository.findByTransactionId(transactionId)
                .orElseThrow(()-> new TransactionNotFoundException(transactionId));
        // Verifies that the transaction belongs to the invoice.
        if (!invoice.getTransaction().getId().equals(transactionId)){
            throw new TransactionDoesNotMatchException(transactionId);
        }
        return invoice;
    }
}
