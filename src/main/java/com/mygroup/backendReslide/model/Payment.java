package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_payment")
    @SequenceGenerator(name="generator_payment", sequenceName = "sequence_payment")
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user; // User who registers the payment.

    private Instant date;

    @OneToOne
    @JoinColumn(name = "paymentMethodId", referencedColumnName = "id")
    private PaymentMethod paymentMethod;

    private BigDecimal paid;

    private BigDecimal owedBefore; // Amount owed before the payment.

    private BigDecimal owedAfter; // Amount owed after the payment.

    @Column(nullable = false)
    private PaymentStatus status;

    @Column(columnDefinition = "text")
    private String notes;

}
