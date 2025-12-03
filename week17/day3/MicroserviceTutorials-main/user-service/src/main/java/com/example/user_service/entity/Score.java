package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quizId;

    private int score;

    private int total;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
