package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.InvoiceDetailRequest;
import com.mygroup.backendReslide.dto.response.InvoiceDetailResponse;
import com.mygroup.backendReslide.exceptions.DiscountNotValidException;
import com.mygroup.backendReslide.exceptions.InvoiceAndDetailDoNotMatchException;
import com.mygroup.backendReslide.exceptions.ProductQuantityException;
import com.mygroup.backendReslide.exceptions.notFound.InvoiceDetailNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.InvoiceNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.mapper.InvoiceDetailMapper;
import com.mygroup.backendReslide.model.*;
import com.mygroup.backendReslide.model.status.InvoiceDetailStatus;
import com.mygroup.backendReslide.model.status.ProductStatus;
import com.mygroup.backendReslide.repository.DiscountRepository;
import com.mygroup.backendReslide.repository.InvoiceDetailRepository;
import com.mygroup.backendReslide.repository.InvoiceRepository;
import com.mygroup.backendReslide.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoiceDetailService {
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceDetailMapper invoiceDetailMapper;
    private final BigDecimal TAX = new BigDecimal(0.13);

    private final DiscountRepository discountRepository;
    // Validates a single detail line.
    @Transactional(readOnly = true)
    public InvoiceDetailResponse validatesRequest(InvoiceDetailRequest invoiceDetailRequest){
        Integer scaleUsed = 2;
        // Searches the product
        Product product = productRepository.findByCodeIgnoreCaseAndProductStatus(invoiceDetailRequest.getProductCode(), ProductStatus.ACTIVE)
                .orElseThrow(()-> new ProductNotFoundException(invoiceDetailRequest.getProductCode()));

        // Verifies if there is more than 0 and there is enough product.
        if(invoiceDetailRequest.getQuantity().compareTo(new BigDecimal(0)) <= 0 ||
        product.getQuantityAvailable().compareTo(invoiceDetailRequest.getQuantity()) == -1){
            throw new ProductQuantityException(invoiceDetailRequest.getProductCode(),invoiceDetailRequest.getQuantity());
        }
        // Maps the request to entity to add the values
        InvoiceDetail invoiceDetail = invoiceDetailMapper.mapToEntity(invoiceDetailRequest);

        // Calculating the amounts.
        invoiceDetail.setSubtotal(product.getPrice().multiply(invoiceDetail.getQuantity()).setScale(scaleUsed, RoundingMode.HALF_UP));
        // Tax exemption
        if(product.getTaxExempt()){
            invoiceDetail.setTax(BigDecimal.ZERO);
            invoiceDetail.setTaxPercentage(BigDecimal.ZERO);
        }else{
            invoiceDetail.setTax(invoiceDetail.getSubtotal().multiply(TAX).setScale(scaleUsed, RoundingMode.HALF_UP));
            invoiceDetail.setTaxPercentage(TAX.multiply(new BigDecimal(100)).setScale(scaleUsed, RoundingMode.HALF_UP));
        }
        invoiceDetail.setTotal(invoiceDetail.getSubtotal().add(invoiceDetail.getTax()));
        // If there is a discount.
        if(invoiceDetail.getDiscountApplied() != null) {
            // Validates the discount
            if(invoiceDetail.getDiscountApplied().getPercentage() <= 0
            || invoiceDetail.getDiscountApplied().getPercentage() > 100){
                throw new DiscountNotValidException(invoiceDetail.getDiscountApplied().getPercentage());
            }
            // Calculates the discount and subtracts it from the total.
            BigDecimal percentage = getPercentage(new BigDecimal(invoiceDetail.getDiscountApplied().getPercentage()));
            invoiceDetail.setDiscount(invoiceDetail.getTotal().multiply(percentage).setScale(scaleUsed, RoundingMode.HALF_UP));
            invoiceDetail.setTotal(invoiceDetail.getTotal().subtract(invoiceDetail.getDiscount()).setScale(scaleUsed, RoundingMode.HALF_UP));
        }else{
            invoiceDetail.setDiscount(BigDecimal.ZERO);
        }
        // Builds and returns the response
        // No data is stored in this method, this is just to check if this invoice detail is valid.
        return invoiceDetailMapper.mapToDto(invoiceDetail);
    }
    // Validates that every invoice detail in a new invoice is valid (quantity, product, discount)
    @Transactional
    public List<InvoiceDetail> validateInvoiceDetails(List<InvoiceDetail> details){
        // Validates every single detail of the request
        // If one detail is not valid, rollbacks the entire transaction.
        return details.stream()
                .map(this:: validateInvoiceDetail)
                .collect(Collectors.toList());
    }
    @Transactional
    private InvoiceDetail validateInvoiceDetail(InvoiceDetail invoiceDetail){
        // Gets the product (previously mapped)
        Product product = invoiceDetail.getProduct();
        // Verifies if there is enough product.
        if(product.getQuantityAvailable().compareTo(invoiceDetail.getQuantity()) == -1
        || invoiceDetail.getQuantity().compareTo(new BigDecimal(0)) <= 0) {
            throw new ProductQuantityException(invoiceDetail.getProduct().getCode(), invoiceDetail.getQuantity());
        }

        // Calculates the amounts.
        invoiceDetail.setPriceByUnit(product.getPrice()); // Price of the item at the time of the purchase
        invoiceDetail.setSubtotal(invoiceDetail.getPriceByUnit().multiply(invoiceDetail.getQuantity()));
        // Tax exemption
        if(product.getTaxExempt()){
            invoiceDetail.setTax(BigDecimal.ZERO);
            invoiceDetail.setTaxPercentage(BigDecimal.ZERO);
        }else{
            invoiceDetail.setTaxPercentage(TAX.multiply(new BigDecimal(100)));
            invoiceDetail.setTax(invoiceDetail.getSubtotal().multiply(getPercentage(invoiceDetail.getTaxPercentage())));
        }
        invoiceDetail.setTotal(invoiceDetail.getSubtotal().add(invoiceDetail.getTax()));

        if(invoiceDetail.getDiscountApplied() != null) {
            // Validates the discount
            if(invoiceDetail.getDiscountApplied().getPercentage() <= 0
                    || invoiceDetail.getDiscountApplied().getPercentage() > 100){
                throw new DiscountNotValidException(invoiceDetail.getDiscountApplied().getPercentage());
            }
            // Calculates the discount and subtracts it from the total.
            BigDecimal percentage = getPercentage(invoiceDetail.getDiscountApplied().getPercentage()); // EX: 30 -> 0.3

            invoiceDetail.setDiscount(invoiceDetail.getTotal().multiply(percentage));
            invoiceDetail.setTotal(invoiceDetail.getTotal().subtract(invoiceDetail.getDiscount()));
            // Store the discount  in the database.
            discountRepository.save(invoiceDetail.getDiscountApplied());
        }else{
            invoiceDetail.setDiscount(BigDecimal.ZERO);
        }

        // Adjust the amount of product available.
        product.setQuantityAvailable(product.getQuantityAvailable().subtract(invoiceDetail.getQuantity()));
        // Stores the new quantity.
        productRepository.save(product);
        return invoiceDetail;
    }
    @Transactional
    public void update(InvoiceDetailRequest invoiceDetailRequest){
        // Maps the request into a old entity to do comparisons.
        InvoiceDetail newInvoiceDetail = invoiceDetailMapper.mapToEntity(invoiceDetailRequest);
        // Searches the invoice detail.
        InvoiceDetail oldInvoiceDetail = invoiceDetailRepository.findById(invoiceDetailRequest.getId())
                .orElseThrow(()-> new InvoiceDetailNotFoundException(invoiceDetailRequest.getId()));
        // Searches the invoice.
        Invoice invoice = invoiceRepository.findById(invoiceDetailRequest.getInvoiceId())
                .orElseThrow(()-> new InvoiceNotFoundException(invoiceDetailRequest.getInvoiceId()));
        // Checks if the invoice has the detail that we want to modify.
        if(!invoice.getDetails().contains(oldInvoiceDetail)){
            throw new InvoiceAndDetailDoNotMatchException(invoiceDetailRequest.getInvoiceId(), invoiceDetailRequest.getInvoiceId());
        }

        // Calculate the product difference in the invoice, then adjust it to the new amount.
        BigDecimal difference = newInvoiceDetail.getQuantity().subtract(oldInvoiceDetail.getQuantity());
        Product product = oldInvoiceDetail.getProduct();

        // Verifies if there is enough product.
        if(product.getQuantityAvailable().compareTo(difference) == -1
                || difference.compareTo(new BigDecimal(0)) == 0) {
            throw new ProductQuantityException(product.getCode(), difference);
        }
        oldInvoiceDetail.setQuantity(newInvoiceDetail.getQuantity());
        // Uses the old product price.
        oldInvoiceDetail.setSubtotal(oldInvoiceDetail.getPriceByUnit().multiply(oldInvoiceDetail.getQuantity()));
        // Tax exemption.
        oldInvoiceDetail.setTaxPercentage(oldInvoiceDetail.getTaxPercentage());
        oldInvoiceDetail.setTax(oldInvoiceDetail.getSubtotal().multiply(getPercentage(oldInvoiceDetail.getTaxPercentage())));
        // Total.
        oldInvoiceDetail.setTotal(oldInvoiceDetail.getSubtotal().add(oldInvoiceDetail.getTax()));
        // Updates / creates the discount
        Discount newDiscount = newInvoiceDetail.getDiscountApplied();
        Discount oldDiscount = oldInvoiceDetail.getDiscountApplied();
        if(newDiscount != null) {
            // Validates the discount
            if(newDiscount.getPercentage() <= 0
                    || newDiscount.getPercentage() > 100){
                throw new DiscountNotValidException(newDiscount.getPercentage());
            }
            // Calculates the discount and subtracts it from the total.
            BigDecimal percentage = getPercentage(newDiscount.getPercentage());
            oldInvoiceDetail.setDiscount(oldInvoiceDetail.getTotal().multiply(percentage));
            oldInvoiceDetail.setTotal(oldInvoiceDetail.getTotal().subtract(oldInvoiceDetail.getDiscount()));
            if(oldDiscount == null){
                // Creates a new discount
                oldInvoiceDetail.setDiscountApplied(newDiscount);
            }else{
                // Update discount.
                oldDiscount.setPercentage(newDiscount.getPercentage());
                oldDiscount.setNotes(newDiscount.getNotes());
                oldDiscount.setReason(newDiscount.getReason());
                // Stores the changes.
                oldInvoiceDetail.setDiscountApplied(oldDiscount);
            }
            discountRepository.save(oldInvoiceDetail.getDiscountApplied());
        }else if(newDiscount == null && oldDiscount != null){
            // Deletes the discount.
            discountRepository.delete(oldDiscount);
            oldInvoiceDetail.setDiscountApplied(null);
            oldInvoiceDetail.setDiscount(BigDecimal.ZERO);
        }else{
            oldInvoiceDetail.setDiscount(BigDecimal.ZERO);
        }

        // Adjust the amount of product available.
        product.setQuantityAvailable(product.getQuantityAvailable().subtract(difference));
        productRepository.save(product);

        // Recalculates the invoice.
        recalculateInvoice(invoice);
    }
    @Transactional
    public void create(InvoiceDetailRequest invoiceDetailRequest) {
        InvoiceDetail invoiceDetail = validateInvoiceDetail(invoiceDetailMapper.mapToEntity(invoiceDetailRequest));
        // Searches the invoice.
        Invoice invoice = invoiceRepository.findById(invoiceDetailRequest.getInvoiceId())
                .orElseThrow(()-> new InvoiceNotFoundException(invoiceDetailRequest.getInvoiceId()));
        // Adds the detail to the invoice.
        invoice.getDetails().add(invoiceDetail);
        invoiceDetailRepository.save(invoiceDetail);
        // Recalculates the invoice
        recalculateInvoice(invoice);
    }
    @Transactional
    public void delete(InvoiceDetailRequest invoiceDetailRequest){
        // Searches the invoice detail that will be deleted.
        InvoiceDetail invoiceDetail = invoiceDetailRepository.findById(invoiceDetailRequest.getId())
                .orElseThrow(()->new InvoiceDetailNotFoundException(invoiceDetailRequest.getId()));
        // Searches the invoice.
        Invoice invoice = invoiceRepository.findById(invoiceDetailRequest.getInvoiceId())
                .orElseThrow(()-> new InvoiceNotFoundException(invoiceDetailRequest.getInvoiceId()));
        // Checks if the invoice has the detail that we want to delete.
        if(invoice.getDetails().stream().noneMatch(invoiceDetailFound -> invoiceDetailFound.getId().equals(invoiceDetail.getId()))){
            throw new InvoiceAndDetailDoNotMatchException(invoiceDetailRequest.getId(), invoiceDetailRequest.getInvoiceId());
        }
        // Deletes the discount, the detail.
        if(invoiceDetail.getDiscountApplied() != null){
            discountRepository.delete(invoiceDetail.getDiscountApplied());
        }
        // Returns the product to stock
        Product product = invoiceDetail.getProduct();
        product.setQuantityAvailable(product.getQuantityAvailable().add(invoiceDetail.getQuantity()));

        // Delete it from database and remove it from the detail list (so the new values can be calculated).
        invoiceDetailRepository.delete(invoiceDetail);
        invoice.getDetails().remove(invoiceDetail);
        recalculateInvoice(invoice);
    }
    @Transactional
    private void recalculateInvoice(Invoice invoice) {

        List<InvoiceDetail> invoiceDetails = invoice.getDetails();
        List<Payment> payments = invoice.getTransaction().getPayments();

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
        // Adds every payment made to the invoice.
        BigDecimal paid = payments.stream()
                .map(payment -> payment.getPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Adds the values to the invoice.
        invoice.setSubtotal(subtotal);
        invoice.setDiscount(discount);
        invoice.setTax(tax);
        invoice.setTotal(total);
        invoice.setPaid(paid);
        invoice.setOwed(total.subtract(paid)); // total - paid
        invoiceRepository.save(invoice);
    }
    @Transactional
    public void updateStatus(InvoiceDetailRequest invoiceDetailRequest){
        // Maps the request into a old entity to do comparisons.
        InvoiceDetail oldInvoiceDetail = invoiceDetailMapper.mapToEntity(invoiceDetailRequest);
        // Searches the invoice detail.
        InvoiceDetail newInvoiceDetail = invoiceDetailRepository.findById(invoiceDetailRequest.getId())
                .orElseThrow(()-> new InvoiceDetailNotFoundException(invoiceDetailRequest.getId()));
        // Searches the invoice.
        Invoice invoice = invoiceRepository.findById(invoiceDetailRequest.getInvoiceId())
                .orElseThrow(()-> new InvoiceNotFoundException(invoiceDetailRequest.getInvoiceId()));
        // Checks if the invoice has the detail that we want to modify.
        if(!invoice.getDetails().contains(newInvoiceDetail)){
            throw new InvoiceAndDetailDoNotMatchException(invoiceDetailRequest.getInvoiceId(), invoiceDetailRequest.getInvoiceId());
        }
        // Searches the product
        Product product = newInvoiceDetail.getProduct();

        // Adjust the amount of product available.
        if(oldInvoiceDetail.getStatus().equals(InvoiceDetailStatus.ACTIVE) &&
                newInvoiceDetail.getStatus().equals(InvoiceDetailStatus.RETURNED)){
            // Grab product from inventory. If there is enough
            if(product.getQuantityAvailable().compareTo(newInvoiceDetail.getQuantity()) < 0){
                throw new ProductQuantityException(product.getCode(), newInvoiceDetail.getQuantity());
            }
            product.setQuantityAvailable(product.getQuantityAvailable().subtract(newInvoiceDetail.getQuantity()));
        }else if(oldInvoiceDetail.getStatus().equals(InvoiceDetailStatus.RETURNED) &&
                newInvoiceDetail.getStatus().equals(InvoiceDetailStatus.ACTIVE)){
            // Return product to inventory
            product.setQuantityAvailable(product.getQuantityAvailable().add(newInvoiceDetail.getQuantity()));
        }
        newInvoiceDetail.setStatus(oldInvoiceDetail.getStatus());
        // Stores the new quantity.
        productRepository.save(product);
        invoiceDetailRepository.save(newInvoiceDetail);
        recalculateInvoice(invoice);
    }

    // Returns a percentage
    private BigDecimal getPercentage(BigDecimal number){
        // EX: 33 -> 0.33
        return number.divide(new BigDecimal(100));
    }
    private BigDecimal getPercentage(Integer number){
        // EX: 33 -> 0.33
        return new BigDecimal(number).divide(new BigDecimal(100));
    }
}
