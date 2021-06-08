package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.request.ProductTypeRequest;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductTypeExistsException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.model.ProductType;
import com.mygroup.backendReslide.model.status.DatabaseStatus;
import com.mygroup.backendReslide.repository.ProductTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    public void register(ProductTypeRequest productTypeRequest){
        // Check whether the product type exists.
        if (productTypeRepository.findByType(productTypeRequest.getType()).isPresent()){
            throw new ProductTypeExistsException(productTypeRequest.getType());
        }
        // Retrieve data from the request, add it to an object and store in the database.
        ProductType productType = new ProductType();
        productType.setType(productTypeRequest.getType());
        productType.setNotes(productTypeRequest.getNotes());
        productType.setStatus(DatabaseStatus.ACTIVE);
        // Store in the database.
        productTypeRepository.save(productType);
    }

    public void update(ProductTypeRequest productTypeRequest){
        // Search the product type.
        ProductType productType = productTypeRepository.findById(productTypeRequest.getId())
                .orElseThrow(()->new ProductTypeNotFoundException(productTypeRequest.getType()));
        // Do the changes.
        productType.setType(productTypeRequest.getType());
        productType.setNotes(productTypeRequest.getNotes());
        // Update the database.
        productTypeRepository.save(productType);
    }

    public void delete(ProductTypeRequest productTypeRequest){
        // Search the product type.
        ProductType productType = productTypeRepository.findById(productTypeRequest.getId())
                .orElseThrow(()-> new ProductTypeNotFoundException(productTypeRequest.getType()));
        // Do the changes.
        productType.setStatus(DatabaseStatus.DELETED);
        // Update the database.
        productTypeRepository.save(productType);
    }



}
