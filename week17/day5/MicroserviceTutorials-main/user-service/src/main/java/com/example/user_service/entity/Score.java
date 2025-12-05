package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "score") 
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quizId;
    private Integer score;
    
    // 1. Remove the @Column annotation. 
    // We will just use "total" in Java and "total" in DB.
    private Integer total; 

    private String reportUrl; 

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}