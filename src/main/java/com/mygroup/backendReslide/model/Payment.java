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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user; // User who registers the payment.

    @NotBlank(message = "Date can't be empty.")
    private Instant date;

    @OneToOne
    @JoinColumn(name = "paymentMethodId", referencedColumnName = "id")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Paid amount can't be empty.")
    private BigDecimal paid;

    @NotBlank(message = "Owed before amount can't be empty.")
    private BigDecimal owedBefore; // Amount owed before the payment.

    @NotBlank(message = "Owed after amount can't be empty.")
    private BigDecimal owedAfter; // Amount owed after the payment.

    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String notes;

}
