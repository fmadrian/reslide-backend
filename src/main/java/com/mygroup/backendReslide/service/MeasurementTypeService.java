package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.MeasurementTypeDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.MeasurementTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.MeasurementTypeNotFoundException;
import com.mygroup.backendReslide.mapper.MeasurementTypeMapper;
import com.mygroup.backendReslide.model.MeasurementType;
import com.mygroup.backendReslide.repository.MeasurementTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeasurementTypeService {

    private final MeasurementTypeRepository measurementTypeRepository;
    private final MeasurementTypeMapper measurementTypeMapper;
    @Transactional
    public MeasurementTypeDto create(MeasurementTypeDto measurementTypeRequest){
        // If it doesn't exist, store it to the database.
        if(measurementTypeRepository.findByNameIgnoreCase(measurementTypeRequest.getName()).isPresent()){
            throw new MeasurementTypeExistsException(measurementTypeRequest.getName());
        }
        MeasurementType measurementType = measurementTypeMapper.mapToEntity(measurementTypeRequest);
        // Store in the database and return it
        measurementType = measurementTypeRepository.save(measurementType);
        return measurementTypeMapper.mapToDto(measurementType);
    }
    @Transactional
    public void update(MeasurementTypeDto measurementTypeRequest){
        MeasurementType measurementType = measurementTypeRepository.findById(measurementTypeRequest.getId())
                .orElseThrow(()->new MeasurementTypeNotFoundException(measurementTypeRequest.getId()));

        // Check if the type already exists.
        if( measurementTypeRepository.findByNameIgnoreCase(measurementTypeRequest.getName()).isPresent()
            && !measurementTypeRequest.getName().equals(measurementType.getName())){
            throw new MeasurementTypeExistsException(measurementTypeRequest.getName());
        }
        // Do the changes and store them.
        measurementType.setName(measurementTypeRequest.getName());
        measurementType.setNotes(measurementTypeRequest.getNotes());

        measurementTypeRepository.save(measurementType);
    }
    @Transactional
    public void switchStatus(MeasurementTypeDto measurementTypeRequest){
        MeasurementType measurementType = measurementTypeRepository.findById(measurementTypeRequest.getId())
                .orElseThrow(()->new MeasurementTypeNotFoundException(measurementTypeRequest.getId()));

        // Do the changes and store them.
        measurementType.setEnabled(!measurementType.isEnabled());

        measurementTypeRepository.save(measurementType);
    }
    @Transactional(readOnly = true)
    public List<MeasurementTypeDto> search(String text) {

        if(text == null){
            // Gets a list of the model (entity) type, maps every element to DTO adds it to a new list
            // and sends it back.
            return measurementTypeRepository.findByEnabled(true)
                    .stream()
                    .map(measurementTypeMapper :: mapToDto)
                    .collect(Collectors.toList());
        }else {
            // Gets a list of the model (entity) type, maps every element to DTO adds it to a new list
            // and sends it back.
            return measurementTypeRepository.findByNameIgnoreCaseContainsOrNotesIgnoreCaseContainsAndEnabled(text, text, true)
                    .stream()
                    .map(measurementTypeMapper::mapToDto)
                    .collect(Collectors.toList());
        }
    }
    @Transactional(readOnly = true)
    public MeasurementTypeDto get(Long id){
        // Find it by id and return it as DTO.
        return measurementTypeMapper.mapToDto(
                measurementTypeRepository.findById(id).orElseThrow(()-> new MeasurementTypeNotFoundException(id))
        );
    }
}
