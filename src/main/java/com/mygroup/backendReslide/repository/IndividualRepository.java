package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface IndividualRepository  extends JpaRepository<Individual, Long> {
    Optional<Individual> findByCodeIgnoreCase(String code);
    List<Individual> findByCodeContainsIgnoreCaseOrNameContainsIgnoreCaseAndEnabled(String code, String name, boolean enabled);
    List<Individual> findByCodeContainsIgnoreCaseAndNameContainsIgnoreCaseAndEnabled(String code, String name, boolean enabled);
    List<Individual> findByEnabled(boolean enabled);
}
