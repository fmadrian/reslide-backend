package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.ContactTypeDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.ContactTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ContactTypeNotFoundException;
import com.mygroup.backendReslide.mapper.ContactTypeMapper;
import com.mygroup.backendReslide.model.ContactType;
import com.mygroup.backendReslide.repository.ContactTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContactTypeService {
    private final ContactTypeMapper contactTypeMapper;
    private final ContactTypeRepository contactTypeRepository;

    @Transactional
    public ContactTypeDto create(ContactTypeDto contactTypeRequest) {
        if(contactTypeRepository.findByTypeIgnoreCase(contactTypeRequest.getType().trim()).isPresent()){
            throw new ContactTypeExistsException(contactTypeRequest.getType());
        }

        ContactType contactType = this.contactTypeRepository.save(contactTypeMapper.mapToEntity(contactTypeRequest));
        return this.contactTypeMapper.mapToDto(contactType);
    }

    @Transactional
    public void update(ContactTypeDto contactTypeRequest){
        ContactType contactType = contactTypeRepository.findById(contactTypeRequest.getId())
                                    .orElseThrow(()-> new ContactTypeNotFoundException(contactTypeRequest.getId()));

        if(contactTypeRepository.findByTypeIgnoreCase(contactTypeRequest.getType().trim()).isPresent()
        && !contactTypeRequest.getType().equals(contactTypeRequest.getType())){
            throw new ContactTypeExistsException(contactTypeRequest.getType());
        }

        contactType.setType(contactTypeRequest.getType());
        contactType.setNotes(contactTypeRequest.getNotes());
        contactType.setEnabled(contactTypeRequest.isEnabled());
        contactTypeRepository.save(contactType);
    }

    @Transactional(readOnly = true)
    public List<ContactTypeDto> search(String type) {
        // If name is null, returns every active method.
        if(type == null){
            return contactTypeRepository.findByEnabled(true)
                    .stream()
                    .map(contactTypeMapper :: mapToDto)
                    .collect(Collectors.toList());
        }
        else {
            return contactTypeRepository.findByTypeContainsIgnoreCaseAndEnabled(type, true)
                    .stream()
                    .map(contactTypeMapper::mapToDto) // .map((contactType) -> contactTypeMapper.mapToDto(contactType))
                    .collect(Collectors.toList());
        }
    }
    @Transactional(readOnly = true)
    public ContactTypeDto get(Long id) {
        return contactTypeMapper.mapToDto(
                contactTypeRepository.findById(id).orElseThrow(()-> new ContactTypeNotFoundException(id))
        );
    }
    @Transactional
    public void switchStatus(ContactTypeDto contactTypeDto) {
        // Search it, reverse the status and save it.
        ContactType contactType = this.contactTypeRepository.findById(contactTypeDto.getId())
                .orElseThrow(()-> new ContactTypeNotFoundException(contactTypeDto.getId()));

        contactType.setEnabled(!contactType.isEnabled());
        this.contactTypeRepository.save(contactType);
    }
}
