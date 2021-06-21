package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.InvoiceDetailDto;
import com.mygroup.backendReslide.dto.InvoiceDto;
import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.exceptions.DiscountNotValidException;
import com.mygroup.backendReslide.exceptions.PaymentExceedsDebtException;
import com.mygroup.backendReslide.exceptions.PaymentQuantityException;
import com.mygroup.backendReslide.exceptions.ProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.InvoiceNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.mapper.InvoiceDetailMapper;
import com.mygroup.backendReslide.mapper.InvoiceMapper;
import com.mygroup.backendReslide.model.*;
import com.mygroup.backendReslide.model.status.InvoiceDetailStatus;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import com.mygroup.backendReslide.model.status.ProductStatus;
import com.mygroup.backendReslide.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceService {
    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;
    private final TransactionRepository transactionRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceDetailService invoiceDetailService;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final BigDecimal TAX = new BigDecimal(0.13);

    @Transactional
    public void create(InvoiceDto invoiceDto) {

        // Maps the invoice dto to entity.
        Invoice invoice = invoiceMapper.mapToEntity(invoiceDto);

        Transaction transaction = invoice.getTransaction();
        // Validates the different invoice details before saving them.
        List<InvoiceDetail> invoiceDetails = invoiceDetailService.validateCreatedInvoiceDetails(invoice.getDetails());

        // The map function help us to return a value from the object
        // That value is added using the reduce function that takes every value returned by map and applies it the add function.
        // Adds every subtotal added to each invoice detail.
        BigDecimal subtotal = invoiceDetails.stream()
                .map(invoiceDetail -> invoiceDetail.getSubtotal()) // .map(invoiceDetail -> {return invoiceDetail.getSubtotal();})
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds every tax added to each invoice detail.
        BigDecimal tax = invoiceDetails.stream()
                .map(invoiceDetail -> invoiceDetail.getTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds every discount added to each invoice detail.
        BigDecimal discount = invoiceDetails.stream()
                .map(invoiceDetail -> invoiceDetail.getDiscount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds every total added to each invoice detail.
        BigDecimal total = invoiceDetails.stream()
                .map(invoiceDetail -> invoiceDetail.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Sets the details to the invoice.
        invoice.setDetails(invoiceDetails);

        // Adds the values to the invoice.
        invoice.setSubtotal(subtotal);
        invoice.setDiscount(discount);
        invoice.setTax(tax);
        invoice.setTotal(total);
        // Set the initial value for owed and paid.
        invoice.setOwed(total);
        invoice.setPaid(BigDecimal.ZERO);
        // Set the payment list to the invoice.
        List<Payment> payments = paymentService.validatePayments(invoice);
        // Adds every payment made to the invoice.
        BigDecimal paid = payments.stream()
                .map(payment-> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Sets the final owed and paid amounts.
        invoice.setPaid(paid);
        invoice.setOwed(total.subtract(paid)); // total - paid

        // It must have a transaction and it must be stored.
        transactionRepository.save(transaction);
        // Saves the invoice details.
        if(!invoiceDetails.isEmpty())
            invoiceDetailRepository.saveAll(invoiceDetails);
        // Stores the payments if there is any.
        if(!transaction.getPayments().isEmpty())
            paymentRepository.saveAll(transaction.getPayments());
        // Save the invoice.
        invoiceRepository.save(invoice);
    }

    // Returns an specific invoice with its details.
    @Transactional(readOnly = true)
    public InvoiceDto get(Long id) {
        return invoiceMapper.mapToDto(
                invoiceRepository.findById(id).orElseThrow(()-> new InvoiceNotFoundException(id))
        );
    }
    // Search invoice(s) functions.
    // We are hiding the invoice details to only show them when we return an specific invoice.
    @Transactional(readOnly = true)
    public List<InvoiceDto> search(String start, String end) {
        return invoiceRepository.findByDate(Instant.parse(start), Instant.parse(end))
                .stream()
                .map(invoiceMapper :: mapToDto)
                .map(this :: hideInvoiceDetails) // Hide the invoice details.
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<InvoiceDto> searchByClient(String start, String end, String clientCode) {
        return invoiceRepository.findByDateAndClientCode(Instant.parse(start), Instant.parse(end),clientCode)
                .stream()
                .map(invoiceMapper :: mapToDto)
                .map(this :: hideInvoiceDetails) // Hide the invoice details.
                .collect(Collectors.toList());
    }

    private InvoiceDto hideInvoiceDetails(InvoiceDto invoiceDto){
        invoiceDto.setDetails(null);
        return invoiceDto;
    }
}
