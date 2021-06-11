package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ProductDto;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.status.ProductStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "measurementType", ignore = true)
    @Mapping(target = "productStatus", ignore = true)
    public abstract Product mapToEntity(ProductDto productDto);

    @Mapping(target = "brand", expression = "java(product.getBrand().getName())")
    @Mapping(target = "type", expression = "java(product.getType().getType())")
    @Mapping(target = "measurementType", expression = "java(product.getMeasurementType().getName())")
    @Mapping(target = "productStatus", expression = "java(getProductStatus(product.getProductStatus()))")
    public abstract ProductDto mapToDto(Product product);


    String getProductStatus(ProductStatus productStatus){
        return productStatus.getStatus();
    }
}
