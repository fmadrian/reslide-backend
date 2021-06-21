package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.Invoice;
import com.mygroup.backendReslide.model.Transaction;
import com.mygroup.backendReslide.model.status.InvoiceStatus;
import com.mygroup.backendReslide.model.status.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Queries defined to get invoices using the transaction date.
    @Query(value = "SELECT * from \"fn_findByDate\"(:start, :end)",
    nativeQuery = true)
    List<Invoice> findByDate(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = "SELECT * from \"fn_findByDateAndClientCode\"(:start, :end, :clientCode)",
            nativeQuery = true)
    List<Invoice> findByDateAndClientCode(@Param("start")Instant start, @Param("end")Instant end,
                                      @Param("clientCode")String clientCode);

}
