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

public class ContactType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_contact_type")
    @SequenceGenerator(name="generator_contact_type", sequenceName = "sequence_contact_type")
    private Long id;

    @NotBlank(message = "Type can't be empty.")
    private String type;

    @Column(nullable = false)
    private boolean enabled;
    @Column(columnDefinition = "text")
    private String notes;
}
