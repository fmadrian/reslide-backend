package com.mygroup.backendReslide.model;

import com.mygroup.backendReslide.model.status.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_reslide") // User is a reserved keyword in postgresql.
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Instant created; // User creation date.

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId", referencedColumnName = "id")
    private Individual individual; // Person related to the account.
}
