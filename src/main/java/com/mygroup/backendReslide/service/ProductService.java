package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.dto.ProductDto;
import com.mygroup.backendReslide.exceptions.alreadyExists.ProductExistsException;
import com.mygroup.backendReslide.exceptions.notFound.MeasurementTypeNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductBrandNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductNotFoundException;
import com.mygroup.backendReslide.exceptions.notFound.ProductTypeNotFoundException;
import com.mygroup.backendReslide.mapper.ProductMapper;
import com.mygroup.backendReslide.model.MeasurementType;
import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.ProductBrand;
import com.mygroup.backendReslide.model.ProductType;
import com.mygroup.backendReslide.model.status.ProductStatus;
import com.mygroup.backendReslide.repository.MeasurementTypeRepository;
import com.mygroup.backendReslide.repository.ProductBrandRepository;
import com.mygroup.backendReslide.repository.ProductRepository;
import com.mygroup.backendReslide.repository.ProductTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private final ProductBrandRepository productBrandRepository;
    private final ProductTypeRepository productTypeRepository;
    private final MeasurementTypeRepository measurementTypeRepository;
    @Transactional
    public void create(ProductDto productRequest){
        // Validate the code.
        if(productRepository.findByCodeIgnoreCase(productRequest.getCode()).isPresent()){
            throw new ProductExistsException(productRequest.getCode());
        }
        Product product = productMapper.mapToEntity(productRequest);
        // Search the product brand.
        ProductBrand productBrand = productBrandRepository.findByNameIgnoreCase(productRequest.getBrand())
                .orElseThrow(()->new ProductBrandNotFoundException(productRequest.getBrand()));
        // Search the product type.
        ProductType productType = productTypeRepository.findByTypeIgnoreCase(productRequest.getType())
                .orElseThrow(()->new ProductTypeNotFoundException(productRequest.getType()));
        // Search the measurement type.
        MeasurementType measurementType = measurementTypeRepository.findByNameIgnoreCase(productRequest.getMeasurementType())
                .orElseThrow(()->new MeasurementTypeNotFoundException(productRequest.getMeasurementType()));

        // Set the brand, type, measurement and status.
        product.setBrand(productBrand);
        product.setType(productType);
        product.setMeasurementType(measurementType);
        product.setProductStatus(ProductStatus.ACTIVE);

        // Store it.
        productRepository.save(product);
    }
    @Transactional
    public void update(ProductDto productRequest){
        // Validate the code.
        if(productRepository.findByCodeIgnoreCase(productRequest.getCode()).isPresent()){
            throw new ProductExistsException(productRequest.getCode());
        }
        // Search the product
        Product product = productRepository.findById(productRequest.getId())
                .orElseThrow(()-> new ProductNotFoundException(productRequest.getId()));
        // Search the product brand.
        ProductBrand productBrand = productBrandRepository.findByNameIgnoreCase(productRequest.getBrand())
                .orElseThrow(()->new ProductBrandNotFoundException(productRequest.getBrand()));
        // Search the product type.
        ProductType productType = productTypeRepository.findByTypeIgnoreCase(productRequest.getType())
                .orElseThrow(()->new ProductTypeNotFoundException(productRequest.getType()));
        // Search the measurement type.
        MeasurementType measurementType = measurementTypeRepository.findByNameIgnoreCase(productRequest.getMeasurementType())
                .orElseThrow(()->new MeasurementTypeNotFoundException(productRequest.getMeasurementType()));
        // Gets the product status
        ProductStatus productStatus = ProductStatus.valueOf(productRequest.getProductStatus());

        // Do the changes
        product.setBrand(productBrand);
        product.setType(productType);
        product.setCode(productRequest.getCode());
        product.setName(productRequest.getName());
        product.setMeasurementType(measurementType);
        product.setQuantityAvailable(productRequest.getQuantityAvailable());
        product.setPrice(productRequest.getPrice());
        product.setNotes(productRequest.getNotes());
        product.setProductStatus(productStatus);

        // Store them.
        productRepository.save(product);
    }
    // Switches the product from one state to other.
    @Transactional(readOnly = true)
    public List<ProductDto> searchAll(){
        return productRepository.findAll()
                .stream()
                .map(productMapper :: mapToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> search(String name, String code, String brand, String type, String status) {
        ProductBrand productBrand = null;
        ProductType productType = null;
        ProductStatus productStatus = null;

        // Search the product brand if the parameter is sent.
        if(brand != null){
            productBrand = productBrandRepository.findByNameIgnoreCase(brand)
                    .orElseThrow(()->new ProductBrandNotFoundException(brand));
        }
        // Search the product type if the parameter is sent.
        if(type != null) {
            productType = productTypeRepository.findByTypeIgnoreCase(type)
                    .orElseThrow(() -> new ProductTypeNotFoundException(type));
        }
        // Search by product status if the parameter is sent.
        if(status != null) {
            productStatus = ProductStatus.valueOf(status.toUpperCase(Locale.ROOT));
        }

        List<Product> result = null;
        // Searching with status.
        if(productStatus != null) {
            if(productType != null) {
                if (productBrand != null) {
                    // Brand, type, status, name and code.
                    result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandAndTypeAndProductStatusOrderByCodeDesc(code, name, productBrand, productType, productStatus);
                } else {
                    // Type, status, name and code.
                    result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeAndProductStatusOrderByCodeDesc(code, name, productType, productStatus);
                }
            }else if (productBrand != null) {
                  // Brand, status, name and code.
                result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandAndProductStatusOrderByCodeDesc(code, name, productBrand, productStatus);
            } else {
                // Status, name and code.
                result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndProductStatusOrderByCodeDesc(code, name, productStatus);
            }
        }else{ // Without status
            if(productBrand != null){
                if(productType != null){
                    // Brand, type, name and code.
                    result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeAndBrandOrderByCodeDesc(code, name, productType, productBrand);
                }else{
                    // Brand, name and code.
                    result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandOrderByCodeDesc(code, name, productBrand);
                }
            }else if(productType != null){
                    // Type, name and code.
                    result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeOrderByCodeDesc(code, name, productType);
            }else{
                // Name and code.
                result = productRepository.findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsOrderByCodeDesc(code, name);
            }
        }

        // Map the result and return it.
        return result.stream()
                .map(productMapper :: mapToDto)
                .collect(Collectors.toList());
    }
}