package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.DatabaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String description;

    @NotBlank(message = "An address can't be empty.")
    private String value;

    @Column(nullable = false)
    private DatabaseStatus status;
}