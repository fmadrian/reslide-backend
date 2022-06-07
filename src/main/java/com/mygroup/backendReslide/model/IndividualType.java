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
public class IndividualType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_individual_type")
    @SequenceGenerator(name="generator_individual_type", sequenceName = "sequence_individual_type")
    private Long id;

    @NotBlank(message = "Name can't be empty")
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "text")
    private String notes;
}
