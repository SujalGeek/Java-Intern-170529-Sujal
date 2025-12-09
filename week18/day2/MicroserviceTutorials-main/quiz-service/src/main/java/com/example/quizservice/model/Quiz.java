package com.example.quizservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    
    private String categoryName; // Store for display
    private Integer numQuestions; // Store for display

    @ElementCollection
    private List<Integer> questionIds;
}