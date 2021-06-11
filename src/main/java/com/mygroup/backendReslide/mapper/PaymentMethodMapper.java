package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)")
    PaymentMethod mapToEntity(PaymentMethodDto paymentMethodDto);

    PaymentMethodDto mapToDto(PaymentMethod paymentType);
}
