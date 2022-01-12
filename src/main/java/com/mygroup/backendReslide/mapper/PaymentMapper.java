package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.PaymentMethod;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.service.PaymentMethodService;
import com.mygroup.backendReslide.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentMethodService paymentMethodService;

    @Mapping(target = "user", expression = "java(getUser(paymentDto.getUsername()))")
    @Mapping(target = "paymentMethod", expression = "java(getPaymentMethod(paymentDto.getPaymentMethod()))")
    public abstract Payment mapToEntity(PaymentDto paymentDto);

    @Mapping(target = "username", expression = "java(payment.getUser().getUsername())")
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod().getName())")
    @Mapping(target = "date", expression = "java(payment.getDate().toString())")
    public abstract PaymentDto mapToDto(Payment payment);

    // Services can't be used by the Impl class
    // User service function can't be used by the Impl class
    // Mappers can't throw EXCEPTIONS it provokes a UnexpectedRollbackException
    User getUser(String username){
        if(username != null && !username.isEmpty()){
            return userService.getUser_Entity(username);
        }
        return null;
    }
    PaymentMethod getPaymentMethod(String name){return paymentMethodService.getPaymentMethod_Entity(name);}

}
