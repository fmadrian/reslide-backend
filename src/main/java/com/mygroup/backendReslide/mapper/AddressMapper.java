package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "enabled", expression = "java(true)")
    Address mapToEntity(AddressDto addressDto);

    AddressDto mapToDto(Address address);
}
