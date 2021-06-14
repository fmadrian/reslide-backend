package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.IndividualDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualCodeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualNotFoundException;
import com.mygroup.backendReslide.mapper.IndividualMapper;
import com.mygroup.backendReslide.model.Address;
import com.mygroup.backendReslide.model.Contact;
import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.repository.AddressRepository;
import com.mygroup.backendReslide.repository.ContactRepository;
import com.mygroup.backendReslide.repository.IndividualRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IndividualService {
    private final IndividualMapper individualMapper;
    private final IndividualRepository individualRepository;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public void create(IndividualDto individualDto) {
        // Check whether the individual exists or not.
        if(individualRepository.findByCodeIgnoreCase(individualDto.getCode()).isPresent()){
            throw new IndividualCodeExistsException(individualDto.getCode());
        }
        // Map the request
        Individual individual = individualMapper.mapToEntity(individualDto);
        // Separate the contacts and addresses, so we can save them by themselves.
        List<Contact> contacts = individual.getContacts();
        List<Address> addresses = individual.getAddresses();

        // Save the contacts and the addresses, then save the individual.
        if(!contacts.isEmpty())
            contactRepository.saveAll(contacts);
        if(!addresses.isEmpty())
            addressRepository.saveAll(addresses);
        // Save the individual.
        individualRepository.save(individual);
    }

    public void update(IndividualDto individualDto) {
        // Check whether the individual exists or not.
        individualRepository.findById(individualDto.getId())
                .orElseThrow(()->new IndividualNotFoundException(individualDto.getId()));
        // If it exists, modify it.
        Individual individual = individualMapper.mapToEntity(individualDto);
        // Check whether the code has been used (by other individual).
        if(individualRepository.findByCodeIgnoreCase(individualDto.getCode()).isPresent()
        && !individualDto.getCode().equals(individual.getCode())){
            throw new IndividualCodeExistsException(individualDto.getCode());
        }
        // Separates the contacts and addresses to update them by themselves.
        List<Contact> contacts = individual.getContacts();
        List<Address> addresses = individual.getAddresses();

        // Update contacts and addresses.
        // REMEMBER: THIS UPDATE ALSO ADDS ANY NEW CONTACT / ADDRESS (CONTACT / ADDRESS WITHOUT ID) THAT COMES IN THE REQUEST.
        if(!contacts.isEmpty())
            contactRepository.saveAll(contacts);
        if(!addresses.isEmpty())
            addressRepository.saveAll(addresses);

        // Updates the individual.
        individualRepository.save(individual);
    }

    public List<IndividualDto> search(String query) {
        if(query.isEmpty()) {
            // Get all
            return individualRepository.findByEnabled(true)
                    .stream()
                    .map(individualMapper :: mapToDto)
                    .collect(Collectors.toList());
        }else{
            return individualRepository.findByCodeContainsIgnoreCaseOrNameContainsIgnoreCaseAndEnabled(query, query, true)
                    .stream()
                    .map(individualMapper :: mapToDto)
                    .collect(Collectors.toList());
        }
    }
}
