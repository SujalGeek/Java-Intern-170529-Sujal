package com.fitness.userservice.models;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id; // MongoDB auto-generates a unique String ObjectID

    @Indexed(unique = true) //  Replaces @Column(unique = true)
    private String email;

    private String password; // @Column(nullable=false) is removed (handle validation in DTO)
    private String firstName;
    private String lastName;

    private UserRole userRole = UserRole.USER; // MongoDB stores Enums as Strings by default

    @CreatedDate //  Replaces @CreationTimestamp
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}