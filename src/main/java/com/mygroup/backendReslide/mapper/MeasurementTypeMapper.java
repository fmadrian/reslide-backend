package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.MeasurementTypeDto;
import com.mygroup.backendReslide.model.MeasurementType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeasurementTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression= "java(true)")
    MeasurementType mapToEntity(MeasurementTypeDto measurementTypeDto);

    MeasurementTypeDto mapToDto(MeasurementType measurementType);
}
