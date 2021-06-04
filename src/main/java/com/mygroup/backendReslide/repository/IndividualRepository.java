package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.Individual;
import com.mygroup.backendReslide.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IndividualRepository  extends JpaRepository<Individual, Long> {
    Optional<Individual> findByCode(String code);
}
