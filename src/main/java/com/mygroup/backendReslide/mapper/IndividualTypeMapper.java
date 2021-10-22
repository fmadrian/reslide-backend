package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.model.IndividualType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndividualTypeMapper {
    IndividualType mapToEntity(IndividualTypeDto individualTypeDto);
    IndividualTypeDto mapToDto(IndividualType individualType);
}
