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

public class MeasurementType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_measurement_type")
    @SequenceGenerator(name="generator_measurement_type", sequenceName = "sequence_measurement_type")
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private boolean enabled;
    @Column(columnDefinition = "text")
    private String notes;
}
