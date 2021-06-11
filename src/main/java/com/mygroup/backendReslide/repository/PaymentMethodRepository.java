package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.PaymentMethod;
import com.mygroup.backendReslide.model.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long > {


    Optional<PaymentMethod> findByNameIgnoreCase(String name);

    List<PaymentMethod> findByEnabled(boolean enabled);

    List<PaymentMethod> findByNameIgnoreCaseContainsAndEnabled(String name, boolean enabled);
}