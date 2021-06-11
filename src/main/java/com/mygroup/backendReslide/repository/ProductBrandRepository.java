package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long > {

    Optional<ProductBrand> findByNameIgnoreCase(String name);
    Optional<ProductBrand> findById(Long id);

    List<ProductBrand> findByNameIgnoreCaseContainsAndEnabled(String name, boolean enabled);
    List<ProductBrand> findByEnabled(boolean enabled);

}
