package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ProductBrandDto;
import com.mygroup.backendReslide.model.ProductBrand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.mygroup.backendReslide.model.status.DatabaseStatus;

@Mapper(componentModel = "spring")
public interface ProductBrandMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    ProductBrand mapToEntity(ProductBrandDto productBrandDto);

    ProductBrandDto mapToDto(ProductBrand productBrand);
}
