package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="order_reslide") // Order is a reserved keyword in postgresql.
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transactionId", referencedColumnName = "id")
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "providerId", referencedColumnName = "id")
    private Individual provider;

    private Instant date;
    private Instant expectedDeliveryDate;
    private Instant actualDeliveryDate;

    private BigDecimal total;
    private BigDecimal paid;
    private BigDecimal owed;

    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "id")
    private List<OrderDetail> details;
}
