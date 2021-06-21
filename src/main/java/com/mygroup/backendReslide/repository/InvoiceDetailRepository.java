package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {

}
