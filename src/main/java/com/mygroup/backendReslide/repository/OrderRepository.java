package com.mygroup.backendReslide.repository;


import com.mygroup.backendReslide.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Queries defined to get orders using the transaction date.
    @Query(value = "SELECT * FROM \"fn_findOrderByDate\"(:start, :end)",
            nativeQuery = true)
    List<Order> findByDate(@Param("start") Instant start, @Param("end") Instant end);

    @Query(value = "SELECT * FROM \"fn_findOrderByDateAndProviderCode\"(:start, :end, :providerCode)",
            nativeQuery = true)
    List<Order> findByDateAndProviderCode(@Param("start")Instant start, @Param("end")Instant end,
                                          @Param("providerCode")String providerCode);

    @Query(value = "SELECT * FROM \"fn_findOrderByDateExpectedDateProviderCode\"(:start_date, :end_date,:start_expected_delivery_date, :end_expected_delivery_date, :providerCode)",
            nativeQuery = true)
    List<Order> findOrderByDateExpectedDateProviderCode(@Param("start_date")Instant startDate, @Param("end_date")Instant endDate,
                                                        @Param("start_expected_delivery_date")Instant startExpectedDeliveryDate, @Param("end_expected_delivery_date")Instant endExpectedDeliveryDate,
                                          @Param("providerCode")String providerCode);

    @Query(value = "SELECT * FROM \"fn_findOrderByDateActualDateProviderCode\"(:start_date, :end_date,:start_actual_delivery_date, :end_actual_delivery_date, :providerCode)",
            nativeQuery = true)
    List<Order> findOrderByDateActualDateProviderCode(@Param("start_date")Instant startDate, @Param("end_date")Instant endDate,
                                                        @Param("start_actual_delivery_date")Instant startActualDeliveryDate, @Param("end_actual_delivery_date")Instant endActualDeliveryDate,
                                                        @Param("providerCode")String providerCode);


    @Query(value = "SELECT * FROM \"fn_findOrderByDateExpectedDateActualDateProviderCode\"(:start_date, :end_date,:start_expected_delivery_date, :end_expected_delivery_date,:start_actual_delivery_date, :end_actual_delivery_date, :providerCode)",
            nativeQuery = true)
    List<Order> findOrderByDateExpectedDateActualDateProviderCode(@Param("start_date")Instant startDate, @Param("end_date")Instant endDate,
                                                                  @Param("start_expected_delivery_date")Instant startExpectedDeliveryDate, @Param("end_expected_delivery_date")Instant endExpectedDeliveryDate,
                                                                  @Param("start_actual_delivery_date")Instant startActualDeliveryDate, @Param("end_actual_delivery_date")Instant endActualDeliveryDate,
                                                                  @Param("providerCode")String providerCode);

    @Query(value = "SELECT * FROM \"fn_findOrderByTransactionId\"(:transactionId)",
            nativeQuery = true)
    Optional<Order> findByTransactionId(@Param("transactionId") Long transactionId);
}
