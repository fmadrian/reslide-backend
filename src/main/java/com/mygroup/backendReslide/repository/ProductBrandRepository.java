package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long > {

    Optional<ProductBrand> findByName(String name);
    Optional<ProductBrand> findById(Long id);

    List<ProductBrand> findByNameContainsAndEnabled(String name, boolean enabled);
    List<ProductBrand> findByEnabled(boolean enabled);

}