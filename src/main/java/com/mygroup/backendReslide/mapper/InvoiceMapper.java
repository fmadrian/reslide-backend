package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.dto.request.InvoiceDetailRequest;
import com.mygroup.backendReslide.dto.request.InvoiceRequest;
import com.mygroup.backendReslide.dto.response.InvoiceDetailResponse;
import com.mygroup.backendReslide.dto.response.InvoiceResponse;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.InvoiceDetail;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.InvoiceStatus;
import com.mygroup.backendReslide.service.IndividualService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class InvoiceMapper {

    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private IndividualService individualService;
    @Autowired
    private TransactionMapper transactionMapper;

    @Mapping(target = "client", expression = "java(getClient(invoiceDto.getClientCode()))")
    @Mapping(target = "status", expression = "java(getInvoiceStatus(invoiceDto.getStatus()))")
    @Mapping(target = "details", expression = "java(mapDetailsToEntity(invoiceDto.getDetails()))")
    @Mapping(target = "transaction", expression = "java(mapTransactionToEntity(invoiceDto.getTransaction()))")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "tax", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "owed", ignore = true)
    @Mapping(target = "paid", ignore = true)
    public abstract Invoice mapToEntity(InvoiceRequest invoiceDto);

    @Mapping(target = "clientCode", expression = "java(invoice.getClient().getCode())")
    @Mapping(target = "clientName", expression = "java(invoice.getClient().getName())")
    @Mapping(target = "status", expression = "java(invoice.getStatus().getStatus())")
    @Mapping(target = "details", expression = "java(mapDetailsToDto(invoice.getDetails()))")
    @Mapping(target = "transaction", expression = "java(mapTransactionToDto(invoice.getTransaction()))")
    public abstract InvoiceResponse mapToDto(Invoice invoice);


    Individual getClient(String code){
        return individualService.getIndividual_Entity(code);
    }

    List<InvoiceDetailResponse> mapDetailsToDto(List<InvoiceDetail> details){
        // Map every detail in the invoice detail entity to invoice detail DTO.
        return details.stream()
                .map(invoiceDetailMapper :: mapToDto)
                .collect(Collectors.toList());
    }
    List<InvoiceDetail> mapDetailsToEntity(List<InvoiceDetailRequest> details){
        // Map every detail in the invoice details DTO to invoice detail entity
        return details.stream()
                .map(invoiceDetailMapper :: mapToEntity)
                .collect(Collectors.toList());
    }
    Transaction mapTransactionToEntity(TransactionDto transactionDto){
        return transactionMapper.mapToEntity(transactionDto);
    }
    TransactionDto mapTransactionToDto(Transaction transaction){
        return transactionMapper.mapToDto(transaction);
    }
    InvoiceStatus getInvoiceStatus(String status) {
        if(status != null && status != "") {
            return InvoiceStatus.valueOf(status.toUpperCase(Locale.ROOT));
        }
        return null;
    }

}
