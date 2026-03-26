package com.example.ai_integration_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
    private Long examResultId;

    private Long studentId;
    private Long courseId;

    private Long examQuestionId;

    private String studentAnswer;

    private BigDecimal score;

    private String feedback;

    private String bloomLevel;
    private Integer marks;
    
    private LocalDateTime createdAt;
}
