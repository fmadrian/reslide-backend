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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Name can't be empty.")
    private String name;

    private String notes;

    @Column(nullable = false)
    private boolean enabled;
}
