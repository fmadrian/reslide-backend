package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    Optional<ProductType> findByType(String type);
    List<ProductType> findByTypeContainsAndEnabled(String type, boolean enabled);
    List<ProductType> findByEnabled(boolean enabled);
    void deleteById(Long id);
}
