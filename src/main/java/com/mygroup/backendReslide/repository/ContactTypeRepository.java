package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactTypeRepository extends JpaRepository<ContactType, Long> {
    Optional<ContactType> findByTypeIgnoreCase(String type);
    List<ContactType> findByTypeContainsIgnoreCaseAndStatus(String type, boolean status);


    List<ContactType> findByStatus(boolean status);
}
