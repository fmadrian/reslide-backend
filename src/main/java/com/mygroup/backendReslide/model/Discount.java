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

public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_discount")
    @SequenceGenerator(name="generator_discount", sequenceName = "sequence_discount")
    private Long id;

    private Integer percentage; // Between 1 and 100.

    @NotBlank(message = "A reason is required.")
    private String reason;

    @Column(columnDefinition = "text")
    private String notes;
}
