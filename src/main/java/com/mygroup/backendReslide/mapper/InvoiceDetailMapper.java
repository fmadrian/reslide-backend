package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.DiscountDto;
import com.mygroup.backendReslide.dto.InvoiceDetailDto;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.model.Discount;
import com.mygroup.backendReslide.model.InvoiceDetail;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.InvoiceDetailStatus;
import com.mygroup.backendReslide.repository.ProductRepository;
import com.mygroup.backendReslide.service.ProductService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Mapper(componentModel = "spring")
public abstract class InvoiceDetailMapper {

    @Autowired
    private ProductService productService;
    @Autowired
    private DiscountMapper discountMapper;

    @Mapping(target = "product", expression = "java(getProduct(invoiceDetailDto.getProductCode()))")
    @Mapping(target = "discountApplied", expression = "java(mapDiscountToEntity(invoiceDetailDto.getDiscountApplied()))")
    @Mapping(target = "status",
            expression = "java(getInvoiceDetailStatus(invoiceDetailDto.getStatus()))")
    public abstract InvoiceDetail mapToEntity(InvoiceDetailDto invoiceDetailDto);

    @Mapping(target = "productCode", expression = "java(invoiceDetail.getProduct().getCode())")
    @Mapping(target = "discountApplied", expression = "java(mapDiscountToDto(invoiceDetail.getDiscountApplied()))")
    @Mapping(target = "status", expression = "java(invoiceDetail.getStatus().getStatus())")
    public abstract InvoiceDetailDto mapToDto(InvoiceDetail invoiceDetail);

    Discount mapDiscountToEntity(DiscountDto discountDto){
        return discountMapper.mapToEntity(discountDto);
    }
    DiscountDto mapDiscountToDto(Discount discount){
        return discountMapper.mapToDto(discount);
    }
    InvoiceDetailStatus getInvoiceDetailStatus(String status){
        return InvoiceDetailStatus.valueOf(status.toUpperCase(Locale.ROOT));
    }
    Product getProduct(String code){
        return productService.getProduct_Entity(code);
    }
}
