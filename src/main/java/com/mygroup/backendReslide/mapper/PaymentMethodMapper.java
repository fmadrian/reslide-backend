package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.PaymentMethodDto;
import com.mygroup.backendReslide.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethod mapToEntity(PaymentMethodDto paymentMethodDto);

    PaymentMethodDto mapToDto(PaymentMethod paymentType);
}
