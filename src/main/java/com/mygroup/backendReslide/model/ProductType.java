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
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Type is required")
    @Column(unique = true)
    private String type;

    @Column(nullable = false)
    private DatabaseStatus status;

    @Column(nullable = false)
    private String notes;
}
