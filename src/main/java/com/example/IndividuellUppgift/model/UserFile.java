package com.example.IndividuellUppgift.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a file. A file is always connected with a user.
 */
@Entity
@Data
@Table
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_file_user"))
    private User user;
}
