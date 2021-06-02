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

    @NotBlank(message = "Quantity can't be empty.")
    private BigDecimal quantity;

    @NotBlank(message = "Subtotal can't be empty.")
    private BigDecimal subtotal;

    @NotBlank(message = "Tax can't be empty.")
    private BigDecimal tax;

    @NotBlank(message = "Discount can't be empty.")
    private BigDecimal discount;

    @NotBlank(message = "Total can't be empty.")
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discountId", referencedColumnName = "id")
    private Discount discountApplied;

    @Column(nullable = false)
    private InvoiceDetailStatus status;

    @Column(nullable = false)
    private String notes;
}
