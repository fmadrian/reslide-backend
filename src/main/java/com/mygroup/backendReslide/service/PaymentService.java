package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.exceptions.*;
import com.mygroup.backendReslide.exceptions.notFound.PaymentNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.TransactionNotFoundException;
import com.mygroup.backendReslide.mapper.PaymentMapper;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import com.mygroup.backendReslide.repository.InvoiceRepository;
import com.mygroup.backendReslide.repository.OrderRepository;
import com.mygroup.backendReslide.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
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
            // Order payment
            // Searches the order
            Order order = getOrder(transactionId);
            // Maps the new payments and validates them.
            payment = validateOrderPayment(payment, order);
            // Store payments and recalculate the invoice.
            paymentRepository.save(payment);
            order.getTransaction().getPayments().add(payment);
            recalculateOrder(order);
        }
    }

    @Transactional
    public void update(PaymentDto paymentRequest){
        Long transactionId = paymentRequest.getTransactionId();
        Payment newPayment = paymentMapper.mapToEntity(paymentRequest);
        Payment oldPayment = paymentRepository.findById(paymentRequest.getId())
                .orElseThrow(() -> new PaymentNotFoundException(paymentRequest.getId()));
        if(oldPayment.getStatus().equals(PaymentStatus.OVERTURNED)){
            throw new PaymentOverturnedException(oldPayment.getId());
        }
        try {
            // Search invoice
            Invoice invoice = getInvoice(transactionId);
            checkPaymentBelongsToTransaction(oldPayment, invoice.getTransaction());
            // Rollback this payment.
            invoice.setPaid(invoice.getPaid().subtract(oldPayment.getPaid())); // paid (invoice) - payment
            // Change how much is owed before doing the validation.
            invoice.setOwed(invoice.getTotal().subtract(invoice.getPaid()));
            // Set the new amount.
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
            // Orders
            Order order = getOrder(transactionId);
            checkPaymentBelongsToTransaction(oldPayment, order.getTransaction());
            // Rollback this payment
            order.setPaid(order.getPaid().subtract(oldPayment.getPaid())); // paid (order) - payment
            order.setOwed(order.getTotal().subtract(order.getPaid())); //  owed - payment
            // Change the amount and tries to validate it
            oldPayment.setPaid(newPayment.getPaid());
            oldPayment = validateOrderPayment(oldPayment, order);
            // Change the remaining details.
            oldPayment.setPaymentMethod(newPayment.getPaymentMethod());
            oldPayment.setDate(newPayment.getDate());
            oldPayment.setNotes(newPayment.getNotes());
            // Save the changes.
            paymentRepository.save(oldPayment);
            recalculateOrder(order);
        }
    }

    @Transactional
    public void overturn(PaymentDto paymentRequest) {
        Long transactionId = paymentRequest.getTransactionId();
        Payment payment = paymentRepository.findById(paymentRequest.getId())
                .orElseThrow(()->new PaymentNotFoundException(paymentRequest.getId()));

        if(!payment.getStatus().equals(PaymentStatus.ACTIVE)){
            throw new PaymentOverturnedException(payment.getId());
        }
        try{
            // Search invoice
            Invoice invoice = getInvoice(transactionId);
            checkPaymentBelongsToTransaction(payment,invoice.getTransaction());
            // Change the status of the payment
            payment.setStatus(PaymentStatus.OVERTURNED);
            // The payment was overturned, therefore the amount owed doesn't change.
            payment.setOwedAfter(payment.getPaid());
            // Store the changes.
            paymentRepository.save(payment);
            recalculateInvoice(invoice);
        }catch (TransactionNotFoundException e){
            // Orders
            Order order = getOrder(transactionId);
            checkPaymentBelongsToTransaction(payment,order.getTransaction());
            // Change the status of the payment
            payment.setStatus(PaymentStatus.OVERTURNED);
            // The payment was overturned, therefore the amount owed doesn't change.
            payment.setOwedAfter(order.getOwed().add(payment.getPaid()));
            // Store the changes.
            paymentRepository.save(payment);
            recalculateOrder(order);
        }
    }

    @Transactional
    private void recalculateInvoice(Invoice invoice){
        List<Payment> payments = invoice.getTransaction().getPayments();
        // Only has to count active payments, gets the amount paid in every payment and adds it.
        BigDecimal paid = payments.stream()
                .filter(payment -> payment.getStatus().equals(PaymentStatus.ACTIVE))
                .map(payment -> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds the values to the invoice.
        invoice.setPaid(paid);
        invoice.setOwed(invoice.getTotal().subtract(paid)); // total - paid
        invoiceRepository.save(invoice);
    }
    @Transactional
    private void recalculateOrder(Order order){
        List<Payment> payments = order.getTransaction().getPayments();
        // Only has to count active payments, gets the amount paid in every payment and finally, adds them.
        BigDecimal paid = payments.stream()
                .filter(payment -> payment.getStatus().equals(PaymentStatus.ACTIVE))
                .map(payment -> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds the values to the invoice.
        order.setPaid(paid);
        order.setOwed(order.getTotal().subtract(paid)); // total - paid
        orderRepository.save(order);
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
        payment.setUser(authService.getCurrentUser());
        // Payment can't exceed the debt.
        if(payment.getPaid().compareTo(invoice.getOwed()) == 1){
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
    @Transactional
    public List<Payment> validateOrderPayments(List<Payment> payments, Order order) {
        return payments.stream()
                .map(payment -> validateOrderPayment(payment, order))
                .collect(Collectors.toList());
    }
    @Transactional
    public Payment validateOrderPayment(Payment payment, Order order){
        payment.setUser(authService.getCurrentUser());
        // Payment can't exceed the debt.
        if(payment.getPaid().compareTo(order.getOwed()) == 1){
            throw new PaymentExceedsDebtException(payment.getPaid(), order.getOwed());
        }
        if(payment.getPaid().compareTo(BigDecimal.ZERO) != 1){
            throw new PaymentQuantityException(payment.getPaid());
        }
        // Adjust the owed value in the invoice.
        // The invoice will be changed in the invoice create method.
        payment.setOwedBefore(order.getOwed());
        // Change made in the invoice, just as a reference for the next payment line (if there is any).
        order.setOwed(order.getOwed().subtract(payment.getPaid()));
        payment.setOwedAfter(order.getOwed());
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
    private Order getOrder(Long transactionId){
        // Search invoice
        Order order = orderRepository.findByTransactionId(transactionId)
                .orElseThrow(()-> new TransactionNotFoundException(transactionId));
        // Verifies that the transaction belongs to the invoice.
        if (!order.getTransaction().getId().equals(transactionId)){
            throw new TransactionDoesNotMatchException(transactionId);
        }
        return order;
    }
    private void checkPaymentBelongsToTransaction(Payment payment, Transaction transaction){
        if(!transaction.getPayments().contains(payment)){
            throw new PaymentAndTransactionDoNotMatch(payment.getId(), transaction.getId());
        }
    }
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByDate(String type, String startDate, String endDate) {
        if(type.equals("order")){
            return this.getOrderPaymentsByDate(startDate, endDate);
        }else if(type.equals("invoice")){
            return this.getInvoicePaymentsByDate(startDate, endDate);
        }else if(type.equals("both")){
            // Call both get invoice methods, join the lists and sort them by date.;
            List<PaymentDto> orderPayments = this.getOrderPaymentsByDate(startDate, endDate);
            List<PaymentDto> invoicePayments = this.getInvoicePaymentsByDate(startDate, endDate);
            List<PaymentDto> result = new ArrayList<PaymentDto>();
            result.addAll(orderPayments);
            result.addAll(invoicePayments);
            result = result.stream().sorted(Comparator.comparing(payment -> Instant.parse(payment.getDate()))).collect(Collectors.toList());
            return result;
        }
        else{
            return null;
        }
    }

    @Transactional(readOnly = true)
    private List<PaymentDto> getOrderPaymentsByDate(String startDate, String endDate) {
        // 1. Search the payments made in between the dates.
        // 2. Map them to DTO's (to send them back to the client).
        // 3. Search which payments belong to orders (link a payment with an order)
        // 4. Add the order id to the respective payment dto and return it.
        // 5. Make it a list and return it.
        List<PaymentDto> results = paymentRepository.findByDateBetween(Instant.parse(startDate), Instant.parse(endDate))
                                .stream()
                                .map(paymentMapper::mapToDto)
                                .map(paymentDto -> {
                                    // 3. Search which payments belong to orders (link a payment with an order)
                                    Order order = orderRepository.findByPaymentId(paymentDto.getId()).orElse(null);
                                    if(order != null) {
                                        // 4. Add the order id to the respective payment dto and return it.
                                        paymentDto.setOrderId(order.getId());
                                        return paymentDto;
                                    }return null;
                                }).filter(paymentDto -> paymentDto != null)
                                .collect(Collectors.toList());
        return results;
    }
    @Transactional(readOnly = true)
    private List<PaymentDto> getInvoicePaymentsByDate(String startDate, String endDate) {
        // 1. Search the payments made in between the dates.
        // 2. Map them to DTO's (to send them back to the client).
        // 3. Search which payments belong to invoices (link a payment with an order)
        // 4. Add the invoice id to the respective payment dto and return it.
        // 5. Make it a list and return it.
        List<PaymentDto> results = paymentRepository.findByDateBetween(Instant.parse(startDate), Instant.parse(endDate))
                .stream()
                .map(paymentMapper::mapToDto)
                .map(paymentDto -> {
                    // 3. Search which payments belong to orders (link a payment with an order)
                    Invoice invoice = invoiceRepository.findByPaymentId(paymentDto.getId()).orElse(null);
                    if(invoice != null) {
                        // 4. Add the order id to the respective payment dto and return it.
                        paymentDto.setInvoiceId(invoice.getId());
                        return paymentDto;
                    }return null;
                }).filter(paymentDto -> paymentDto != null)
                .collect(Collectors.toList());
        return results;
    }

}
