package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.dto.TransactionDto;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.model.status.InvoiceStatus;
import com.mygroup.backendReslide.repository.UserRepository;
import com.mygroup.backendReslide.service.UserService;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TransactionMapper {
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private UserService userService;

    @Mapping(target = "payments", expression = "java(mapPaymentsToEntity(transactionDto.getPayments()))")
    @Mapping(target = "user", expression = "java(getUser(transactionDto.getUsername()))")
    public abstract Transaction mapToEntity(TransactionDto transactionDto);

    @Mapping(target = "username", expression = "java(transaction.getUser().getUsername())")
    @Mapping(target = "payments", expression = "java(mapPaymentsToDto(transaction.getPayments()))")
    @Mapping(target = "date", expression = "java(transaction.getDate().toString())")
    public abstract TransactionDto mapToDto(Transaction transaction);

    List<PaymentDto> mapPaymentsToDto(List<Payment> payments){
        return payments.stream()
                .map(paymentMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    List<Payment> mapPaymentsToEntity(List<PaymentDto> payments){
        return payments.stream()
                .map(paymentMapper :: mapToEntity)
                .collect(Collectors.toList());
    }

    // User service function can't be used by the Impl class
    User getUser(String username){
        return userService.getUser_Entity(username);
    }
}

