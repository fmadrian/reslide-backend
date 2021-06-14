package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.AddressDto;
import com.mygroup.backendReslide.dto.ContactDto;
import com.mygroup.backendReslide.exceptions.notFound.AddressNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ContactNotFoundException;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.model.Contact;
import com.mygroup.backendReslide.repository.AddressRepository;
import com.mygroup.backendReslide.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public void delete(ContactDto contactRequest){
        Contact contact = contactRepository.findById(contactRequest.getId())
                .orElseThrow(()-> new ContactNotFoundException(contactRequest.getId()));
        contactRepository.delete(contact);
    }
}
