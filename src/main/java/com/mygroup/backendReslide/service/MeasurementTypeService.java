package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.MeasurementTypeDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.MeasurementTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.MeasurementTypeNotFoundException;
import com.mygroup.backendReslide.mapper.MeasurementTypeMapper;
import com.mygroup.backendReslide.model.MeasurementType;
import com.mygroup.backendReslide.repository.MeasurementTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MeasurementTypeService {

    private final MeasurementTypeRepository measurementTypeRepository;
    private final MeasurementTypeMapper measurementTypeMapper;
    public void create(MeasurementTypeDto measurementTypeRequest){
        // If it doesn't exist, store it to the database.
        if(measurementTypeRepository.findByNameIgnoreCase(measurementTypeRequest.getName()).isPresent()){
            throw new MeasurementTypeExistsException(measurementTypeRequest.getName());
        }
        MeasurementType measurementType = measurementTypeMapper.mapToEntity(measurementTypeRequest);
        measurementTypeRepository.save(measurementType);
    }
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
    public void deactivate(MeasurementTypeDto measurementTypeRequest){
        MeasurementType measurementType = measurementTypeRepository.findById(measurementTypeRequest.getId())
                .orElseThrow(()->new MeasurementTypeNotFoundException(measurementTypeRequest.getId()));

        // Do the changes and store them.
        measurementType.setEnabled(false);

        measurementTypeRepository.save(measurementType);
    }

    public List<MeasurementTypeDto> search() {
        // Gets a list of the model (entity) type, maps every element to DTO adds it to a new list
        // and sends it back.
        return measurementTypeRepository.findByEnabled(true)
                .stream()
                .map(measurementTypeMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    public List<MeasurementTypeDto> searchByText(String text) {
        // Gets a list of the model (entity) type, maps every element to DTO adds it to a new list
        // and sends it back.
        return measurementTypeRepository.findByNameIgnoreCaseContainsOrNotesIgnoreCaseContainsAndEnabled(text, text, true)
                .stream()
                .map(measurementTypeMapper :: mapToDto)
                .collect(Collectors.toList());
    }
}
