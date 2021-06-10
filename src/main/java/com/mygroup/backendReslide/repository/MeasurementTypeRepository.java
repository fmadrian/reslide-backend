package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
    Optional<MeasurementType> findByName(String name);

    List<MeasurementType> findByEnabled(boolean enabled);

    List<MeasurementType> findByNameIgnoreCaseContainsOrNotesIgnoreCaseContainsAndEnabled(String name, String notes, boolean enabled);
}
