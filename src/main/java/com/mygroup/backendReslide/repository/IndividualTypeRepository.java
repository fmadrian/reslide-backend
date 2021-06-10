package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.IndividualType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IndividualTypeRepository extends JpaRepository<IndividualType, Long> {
    Optional<IndividualType> findByNameIgnoreCase(String name);
}
