package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.InvoiceDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Product product;

    private BigDecimal priceByUnit; // Price of the item at the time of the purchase
    private BigDecimal quantity;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discountId", referencedColumnName = "id")
    private Discount discountApplied;

    @Column(nullable = false)
    private InvoiceDetailStatus status;

    @Column(nullable = false)
    private String notes;

    private BigDecimal taxPercentage; // Product exempt from taxes at the time of the transaction will have 0.
}
