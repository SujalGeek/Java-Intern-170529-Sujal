package com.fitness.userservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // MongoDB auto-generates a unique String ObjectID

    @Column(unique = true) //  Replaces @Column(unique = true)
    private String email;

    private String keyCloakId;

    @Column(nullable = false)
    private String password; // @Column(nullable=false) is removed (handle validation in DTO)
    private String firstName;
    private String lastName;

    private UserRole userRole = UserRole.USER; // MongoDB stores Enums as Strings by default

    @CreationTimestamp //  Replaces @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}