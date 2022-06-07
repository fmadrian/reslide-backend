package com.mygroup.backendReslide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_product_brand")
    @SequenceGenerator(name="generator_product_brand", sequenceName = "sequence_product_brand")
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "text")
    private String notes;
}
