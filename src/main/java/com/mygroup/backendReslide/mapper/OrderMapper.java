package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.dto.request.OrderDetailRequest;
import com.mygroup.backendReslide.dto.request.OrderRequest;
import com.mygroup.backendReslide.dto.response.OrderDetailResponse;
import com.mygroup.backendReslide.dto.response.OrderResponse;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.Order;
import com.mygroup.backendReslide.model.OrderDetail;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.OrderStatus;
import com.mygroup.backendReslide.service.IndividualService;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class OrderMapper{
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private IndividualService individualService;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Mapping(target = "provider", expression = "java(getProvider(orderRequest.getProviderCode()))")
    @Mapping(target = "transaction", expression = "java(mapTransactionToEntity(orderRequest.getTransaction()))")
    @Mapping(target = "status", expression = "java(getOrderStatus(orderRequest.getStatus()))")
    public abstract Order mapToEntity(OrderRequest orderRequest);

    @Mapping(target = "transaction", expression = "java(mapTransactionToDto(order.getTransaction()))")
    @Mapping(target = "providerCode", expression = "java(order.getProvider().getCode())")
    @Mapping(target = "providerName",expression = "java(order.getProvider().getName())")
    @Mapping(target = "details", expression = "java(mapOrderDetailsToDto(order.getDetails()))")
    @Mapping(target = "status", expression = "java(order.getStatus().toString())")
    public abstract OrderResponse mapToDto(Order order);

    Transaction mapTransactionToEntity(TransactionDto transactionDto){return transactionMapper.mapToEntity(transactionDto);}
    TransactionDto mapTransactionToDto(Transaction transaction){
        return transactionMapper.mapToDto(transaction);
    }
    Individual getProvider(String code){ return individualService.getIndividual_Entity(code);}
    OrderStatus getOrderStatus(String status) {
        if(status != null && status != "")  {
            return OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
        }
        return null;
    }
    List<OrderDetail> mapOrderDetailsToEntity(List<OrderDetailRequest> details){
        if(details != null){
            return details.stream()
                    .map(orderDetailMapper::mapToEntity)
                    .collect(Collectors.toList());
        }
        return null;
    }
    List<OrderDetailResponse> mapOrderDetailsToDto(List<OrderDetail> details){
        if(details != null) {
            return details.stream()
                    .map(orderDetailMapper::mapToDto)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
