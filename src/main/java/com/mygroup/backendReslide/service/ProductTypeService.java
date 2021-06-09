package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.ProductTypeDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.mapper.ProductTypeMapper;
import com.mygroup.backendReslide.model.ProductType;
import com.mygroup.backendReslide.repository.ProductTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductTypeService {

    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;

    public void create(ProductTypeDto productTypeRequest){
        // Check whether the product type exists.
        if (productTypeRepository.findByType(productTypeRequest.getType()).isPresent()){
            throw new ProductTypeExistsException(productTypeRequest.getType());
        }
        // Retrieve data from the request, add it to an object and store in the database.
        ProductType productType = new ProductType();
        productType.setType(productTypeRequest.getType());
        productType.setNotes(productTypeRequest.getNotes());
        productType.setEnabled(true);
        // Store in the database.
        productTypeRepository.save(productType);
    }

    public void update(ProductTypeDto productTypeRequest){
        // Search the product type by id.
        ProductType productType = productTypeRepository.findById(productTypeRequest.getId())
                .orElseThrow(()->new ProductTypeNotFoundException(productTypeRequest.getId()));
        // Check that the new product type doesn't exist.
        if (productTypeRepository.findByType(productTypeRequest.getType()).isPresent()){
            throw new ProductTypeExistsException(productTypeRequest.getType());
        }
        // Do the changes.
        productType.setType(productTypeRequest.getType());
        productType.setNotes(productTypeRequest.getNotes());
        // Update the database.
        productTypeRepository.save(productType);
    }

    public void deactivate(ProductTypeDto productTypeRequest){
        // Search the product type.
        ProductType productType = productTypeRepository.findById(productTypeRequest.getId())
                .orElseThrow(()-> new ProductTypeNotFoundException(productTypeRequest.getId()));
        // Do the changes.
        productType.setEnabled(false);
        // Update the database.
        productTypeRepository.save(productType);
    }


    public List<ProductTypeDto> getAll() {
        // Returns only active elements.
        List<ProductType> searchList = productTypeRepository.findByEnabled(true);
        // Map the entity list to a DTO list.
        return searchList.stream()
                .map(productTypeMapper :: mapToDto)
                .collect(Collectors.toList());

    }

    public List<ProductTypeDto> getByType(String type) {
        // Returns only active elements.
        List<ProductType> searchList = productTypeRepository.findByTypeContainsAndEnabled(type, true);
        // Map the entity list to a DTO list.
        return searchList.stream()
                .map(productTypeMapper :: mapToDto)
                .collect(Collectors.toList());

    }

    public void delete(ProductTypeDto productTypeRequest) {
        // Searches for the product type and deletes it.
        ProductType productType = productTypeRepository.findById(productTypeRequest.getId())
                .orElseThrow(()-> new ProductTypeNotFoundException(productTypeRequest.getId()));

        productTypeRepository.delete(productType);
    }
}
