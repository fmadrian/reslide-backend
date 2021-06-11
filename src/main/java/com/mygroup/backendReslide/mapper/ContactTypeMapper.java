package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.ContactTypeDto;
import com.mygroup.backendReslide.model.ContactType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", expression = "java(true)")
    ContactType mapToEntity(ContactTypeDto contactTypeRequest);

    ContactTypeDto mapToDto(ContactType contactType);
}
