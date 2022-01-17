package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.exceptions.UserNotAuthorizedException;
import com.mygroup.backendReslide.exceptions.alreadyExists.IndividualTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.IndividualTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.UserNotFoundException;
import com.mygroup.backendReslide.mapper.IndividualTypeMapper;
import com.mygroup.backendReslide.model.IndividualType;
import com.mygroup.backendReslide.model.User;
import com.mygroup.backendReslide.model.status.UserRole;
import com.mygroup.backendReslide.repository.IndividualTypeRepository;
import com.mygroup.backendReslide.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IndividualTypeService {
    private final IndividualTypeRepository individualTypeRepository;
    private final IndividualTypeMapper individualTypeMapper;

    private final AuthService authService;
    private final UserRepository userRepository;

    public List<IndividualTypeDto> getAll(){
        return individualTypeRepository.findByEnabled(true)
                .stream()
                .map(individualTypeMapper :: mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public IndividualTypeDto create(IndividualTypeDto individualTypeDto) {
        this.isUserAuthorized();
        // Verify that it doesn't exist.
        if (individualTypeRepository.findByNameIgnoreCase(individualTypeDto.getName()).isPresent()) {
            throw new IndividualTypeExistsException(individualTypeDto.getName());
        }
        // Create the individual type and save it.
        IndividualType individualType = individualTypeMapper.mapToEntity(individualTypeDto);
        individualType.setEnabled(true);
        individualType = individualTypeRepository.save(individualType);
        // Return it.
        return individualTypeMapper.mapToDto(individualType);
    }
    @Transactional
    public void update(IndividualTypeDto individualTypeDto) {
        this.isUserAuthorized();
        IndividualType individualType = individualTypeRepository.findById(individualTypeDto.getId())
                .orElseThrow(()-> new IndividualTypeNotFoundException(individualTypeDto.getId()));
        // Verify that it doesn't exist.
        if (individualTypeRepository.findByNameIgnoreCase(individualTypeDto.getName()).isPresent() &&
                !individualTypeDto.getName().toUpperCase(Locale.ROOT).equals(individualType.getName().toUpperCase(Locale.ROOT))) {
            throw new IndividualTypeExistsException(individualTypeDto.getName());
        }
        // Update the individual type and save it.
        individualType.setName(individualTypeDto.getName());
        individualType.setNotes(individualTypeDto.getNotes());
        individualTypeRepository.save(individualType);
    }

    public void deactivate(IndividualTypeDto individualTypeDto) {
        // Verify if the user is authorized
        this.isUserAuthorized();
        IndividualType individualType = individualTypeRepository.findById(individualTypeDto.getId())
                .orElseThrow(()-> new IndividualTypeNotFoundException(individualTypeDto.getId()));
        // Deactivate the individual type and save it.
        individualType.setEnabled(!individualType.isEnabled());
        individualTypeRepository.save(individualType);
    }
    @Transactional(readOnly = true)
    public IndividualTypeDto get(Long id){
        return individualTypeMapper.mapToDto(
                individualTypeRepository.findById(id)
                .orElseThrow(()-> new IndividualTypeNotFoundException(id)));
    }
    @Transactional(readOnly = true)
    private void isUserAuthorized(){
        // Roles authorized
        ArrayList<UserRole> authorizedRoles = new ArrayList<UserRole>();
        authorizedRoles.add(UserRole.ADMIN);

        // If the role that the user has is not authorized, throws an exception.
        if (!authorizedRoles.contains(this.authService.getCurrentUser().getRole())){
            throw new UserNotAuthorizedException(this.authService.getCurrentUser().getUsername());
        }
    }


}
