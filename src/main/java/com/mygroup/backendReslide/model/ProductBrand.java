package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.DatabaseStatus;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false)
    private DatabaseStatus status;

    @Column(nullable = false)
    private String notes;
}