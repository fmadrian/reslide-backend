package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
