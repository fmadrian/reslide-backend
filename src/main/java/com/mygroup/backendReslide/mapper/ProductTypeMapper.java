package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ProductTypeDto;
import com.mygroup.backendReslide.model.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ProductTypeMapper  {
    // ProductType mapToEntity(ProductTypeDto productTypeDto);

    ProductTypeDto mapToDto(ProductType productType);
}
