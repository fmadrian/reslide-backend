package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_invoice")
    @SequenceGenerator(name="generator_invoice", sequenceName = "sequence_invoice")
    private Long id;

    @OneToOne
    @JoinColumn(name = "transactionId", referencedColumnName = "id")
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "clientId", referencedColumnName = "id")
    private Individual client;

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal discount;

    private BigDecimal total;

    private BigDecimal paid;

    private BigDecimal owed;

    @Column(nullable = false)
    private InvoiceStatus status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId", referencedColumnName = "id")
    private List<InvoiceDetail> details;
}
