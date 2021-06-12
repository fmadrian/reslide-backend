package com.mygroup.backendReslide.mapper;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.dto.IndividualDto;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.model.Contact;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.IndividualType;
import com.mygroup.backendReslide.repository.IndividualTypeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class IndividualMapper {
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ContactMapper contactMapper;
    @Autowired
    private IndividualTypeRepository individualTypeRepository;

    @Mapping(target = "enabled", expression = "java(true)")
    @Mapping(target = "addresses", expression = "java(mapAddressesToEntity(individualRequest.getAddresses()))")
    @Mapping(target = "contacts", expression = "java(mapContactsToEntity(individualRequest.getContacts()))")
    @Mapping(target = "type", expression = "java(getIndividualType(individualRequest.getType()))")
    public abstract Individual mapToEntity(IndividualDto individualRequest);

    @Mapping(target = "type", expression = "java(individual.getType().getName())")
    @Mapping(target = "addresses", expression = "java(mapAddressesToDto(individual.getAddresses()))")
    @Mapping(target = "contacts", expression = "java(mapContactsToDto(individual.getContacts()))")
    public abstract IndividualDto mapToDto(Individual individual);


    /**
     * IndividualMapper implements functions that take DTO's  of contact and addresses and map them to entities
     * (and vice versa) so they can be stored in the database or sent as a response with the individual object.
     */

    // Address.
    List<Address> mapAddressesToEntity(List<AddressDto> addresses){
        return addresses
                .stream()
                .map(addressMapper :: mapToEntity)
                .collect(Collectors.toList());
    }

    List<AddressDto> mapAddressesToDto(List<Address> addresses){
        return addresses
                .stream()
                .map(addressMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    // Contact.
    List<Contact> mapContactsToEntity(List<ContactDto> contacts){
        return contacts
                .stream()
                .map(contactMapper :: mapToEntity)
                .collect(Collectors.toList());
    }
    List<ContactDto> mapContactsToDto(List<Contact> contacts){
        return contacts
                .stream()
                .map(contactMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    IndividualType getIndividualType(String name){
        return individualTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(()-> new IndividualTypeNotFoundException(name));
    }
}
