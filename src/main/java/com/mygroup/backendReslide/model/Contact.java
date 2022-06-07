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

public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_contact")
    @SequenceGenerator(name="generator_contact", sequenceName = "sequence_contact")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contactTypeId", referencedColumnName = "id")
    private ContactType contactType;

    @NotBlank(message = "Contact can't be empty")
    private String value;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "text")
    private String notes; // Additional notes about this contact.
}
