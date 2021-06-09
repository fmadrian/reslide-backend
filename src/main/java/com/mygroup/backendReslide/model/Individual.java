package com.mygroup.backendReslide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Individual { // Class name Entity was not convenient
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individualTypeId", referencedColumnName = "id")
    private IndividualType type;

    @NotBlank(message = "A code is required for this individual")
    @Column(unique = true)
    private String code; // ID, password, code that allow us to identify this client.

    @NotBlank(message = "A name is required for this individual.")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Contact> contacts;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Address> addresses;

    @Column(nullable = false)
    private boolean status;

    @Column(nullable = false)
    private String notes;
}
