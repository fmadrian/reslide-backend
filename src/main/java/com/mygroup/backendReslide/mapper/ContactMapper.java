package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.exceptions.notFound.ContactTypeNotFoundException;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.model.Contact;
import com.mygroup.backendReslide.model.ContactType;
import com.mygroup.backendReslide.repository.ContactTypeRepository;
import com.mygroup.backendReslide.service.ContactTypeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ContactMapper {
    @Autowired
    private ContactTypeRepository contactTypeRepository;

    @Mapping(target = "enabled", expression = "java(true)")
    @Mapping(target = "contactType", expression = "java(getContactType(contactDto.getContactType()))")
    public abstract Contact mapToEntity(ContactDto contactDto);

    @Mapping(target = "contactType", expression = "java(contact.getContactType().getType())")
    public abstract ContactDto mapToDto(Contact contact);

    ContactType getContactType(String contactType){
        return contactTypeRepository.findByTypeIgnoreCase(contactType)
                .orElseThrow(()-> new ContactTypeNotFoundException(contactType));
    }
}
