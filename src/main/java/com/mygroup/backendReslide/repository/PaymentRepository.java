package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.dto.PaymentDto;
import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Payment;
import com.mygroup.backendReslide.model.status.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDateBetween(Instant startDate, Instant endDate);
    List<Payment> findByDateBetweenAndStatus(Instant startDate, Instant endDate, PaymentStatus status);
}
