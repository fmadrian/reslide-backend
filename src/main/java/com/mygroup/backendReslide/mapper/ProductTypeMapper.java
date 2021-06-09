package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ProductTypeDto;
import com.mygroup.backendReslide.model.ProductType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ProductTypeMapper  {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)")
    ProductType mapToEntity(ProductTypeDto productTypeDto);

    ProductTypeDto mapToDto(ProductType productType);
}
