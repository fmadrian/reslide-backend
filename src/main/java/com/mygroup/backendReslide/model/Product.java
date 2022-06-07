package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_product")
    @SequenceGenerator(name="generator_product", sequenceName = "sequence_product")
    private Long id;

    @JoinColumn(name = "brandId", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductBrand brand;

    @JoinColumn(name = "productTypeId", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductType type;

    @NotBlank(message = "Product code is required.")
    @Column(unique = true)
    private String code;

    @NotBlank(message = "Product name can't be empty.")
    private String name;

    @JoinColumn(name = "measurementTypeId", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MeasurementType measurementType;

    private BigDecimal quantityAvailable;

    private BigDecimal price;

    @Column(nullable = false)
    private ProductStatus productStatus;

    @Column(columnDefinition = "text")
    private String notes;

    private Boolean taxExempt; // Product exempt from taxes.
}
