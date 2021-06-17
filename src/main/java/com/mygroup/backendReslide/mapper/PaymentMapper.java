package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.PaymentDto;
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
    @Mapping(target = "paymentMethod", expression = "java(payment.getPaymentMethod().getName())")
    public abstract PaymentDto mapToDto(Payment payment);

    // Services can't be used by the Impl class
    User getUser(String username){
        return userService.getUser_Entity(username);
    }
    PaymentMethod getPaymentMethod(String name){return paymentMethodService.getPaymentMethod_Entity(name);}
}
