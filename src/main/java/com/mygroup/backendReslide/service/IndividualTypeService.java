package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.IndividualTypeDto;
import com.mygroup.backendReslide.mapper.IndividualTypeMapper;
import com.mygroup.backendReslide.repository.IndividualTypeRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IndividualTypeService {
    private IndividualTypeRepository individualTypeRepository;
    private IndividualTypeMapper individualTypeMapper;
    public List<IndividualTypeDto> getAll(){
        return individualTypeRepository.findByEnabled(true)
                .stream()
                .map(individualTypeMapper :: mapToDto)
                .collect(Collectors.toList());
    }
}
