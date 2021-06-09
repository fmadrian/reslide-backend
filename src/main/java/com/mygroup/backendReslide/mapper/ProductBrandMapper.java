package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ProductBrandDto;
import com.mygroup.backendReslide.model.ProductBrand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductBrandMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    ProductBrand mapToEntity(ProductBrandDto productBrandDto);

    ProductBrandDto mapToDto(ProductBrand productBrand);
}
