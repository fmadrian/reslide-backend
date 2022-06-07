package com.mygroup.backendReslide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_payment_method")
    @SequenceGenerator(name="generator_payment_method", sequenceName = "sequence_payment_method")
    private Long id;

    @NotBlank(message = "Name can't be empty.")
    private String name;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false)
    private boolean enabled;
}
