package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.ProductType;
import com.mygroup.backendReslide.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    Optional<ProductType> findByType(String type);
}
