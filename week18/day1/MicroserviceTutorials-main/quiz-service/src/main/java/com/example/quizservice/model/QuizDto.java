package com.example.quizservice.model;

import lombok.Data;
import java.util.List;

@Data
public class QuizDto {
    String categoryName;
    Integer numQuestions;
    String title;
    
    // NEW: Allow manual selection
    List<Integer> questionIds;
}