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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transactionId", referencedColumnName = "id")
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "clientId", referencedColumnName = "id")
    private Individual client;

    @NotBlank(message = "Subtotal can't be empty.")
    private BigDecimal subtotal;

    @NotBlank(message = "Tax can't be empty.")
    private BigDecimal tax;

    @NotBlank(message = "Discount can't be empty.")
    private BigDecimal discount;

    @NotBlank(message = "Total can't be empty.")
    private BigDecimal total;

    @NotBlank(message = "Paid can't be empty.")
    private BigDecimal paid;

    @NotBlank(message = "Owed can't be empty.")
    private BigDecimal owed;

    @Column(nullable = false)
    private InvoiceStatus status;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId", referencedColumnName = "id")
    private List<InvoiceDetail> details;
}
