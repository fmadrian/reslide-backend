package com.mygroup.backendReslide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_transaction")
    @SequenceGenerator(name="generator_transaction", sequenceName = "sequence_transaction")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user; // User who registered the transaction.

    private Instant date;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "transactionId", referencedColumnName = "id")
    private List<Payment> payments;
}
