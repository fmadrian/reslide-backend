package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.dto.response.OrderDetailResponse;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.InvoiceDetailStatus;
import com.mygroup.backendReslide.model.status.OrderDetailStatus;
import com.mygroup.backendReslide.repository.ProductRepository;
import com.mygroup.backendReslide.service.ProductService;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

@Mapper(componentModel = "spring")
public abstract class OrderDetailMapper {
    @Autowired
    private ProductService productService;

    @Mapping(target = "product", expression = "java(getProduct(orderDetailRequest.getProductCode()))")
    @Mapping(target = "status", expression = "java(getOrderDetailStatus(orderDetailRequest.getStatus()))")
    public abstract OrderDetail mapToEntity(OrderDetailRequest orderDetailRequest);

    @Mapping(target = "productCode", expression = "java(orderDetail.getProduct().getCode())")
    @Mapping(target = "productName", expression = "java(orderDetail.getProduct().getName())")
    @Mapping(target = "status", expression = "java(orderDetail.getStatus().toString())")
    public abstract OrderDetailResponse mapToDto(OrderDetail orderDetail);

    Product getProduct(String code){
        return productService.getProduct_Entity(code);
    }
    OrderDetailStatus getOrderDetailStatus(String status){
        return OrderDetailStatus.valueOf(status.toUpperCase(Locale.ROOT));
    }
}
