package com.mygroup.backendReslide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_product_type")
    @SequenceGenerator(name="generator_product_type", sequenceName = "sequence_product_type")
    private Long id;

    @NotBlank(message = "Type is required")
    @Column(unique = true)
    private String type;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "text")
    private String notes;
}
