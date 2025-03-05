package com.example.IndividuellUppgift.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user.
 */
@Entity
@Data
@Table(name ="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String githubId;

    @Column(nullable = false)
    private String username;

}
