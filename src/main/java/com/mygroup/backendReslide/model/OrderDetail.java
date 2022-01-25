package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.OrderDetailStatus;
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
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private Product product;

    private BigDecimal priceByUnit;
    private BigDecimal quantity;
    private BigDecimal total;

    @Column(nullable = false)
    private OrderDetailStatus status;

    private String notes;
}
