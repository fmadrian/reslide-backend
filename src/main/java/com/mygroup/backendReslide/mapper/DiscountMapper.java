package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.DiscountDto;
import com.mygroup.backendReslide.model.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountDto mapToDto(Discount discount);
    Discount mapToEntity(DiscountDto discountDto);
}
