package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.exceptions.notFound.AddressNotFoundException;
import com.mygroup.backendReslide.mapper.AddressMapper;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public void delete(AddressDto addressRequest){
        Address address = addressRepository.findById(addressRequest.getId())
                .orElseThrow(()-> new AddressNotFoundException(addressRequest.getId()));
        addressRepository.delete(address);
    }
}
