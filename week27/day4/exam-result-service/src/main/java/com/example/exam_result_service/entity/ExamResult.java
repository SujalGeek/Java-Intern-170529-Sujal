package com.example.exam_result_service.entity;

import java.math.BigDecimal; 
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_result_id")
    private Long examResultId;

    @Column(name="student_id",nullable = false)
    private Long studentId;

    @Column(name= "course_id",nullable = false)
    private Long courseId;

    @Column(name="question",nullable = false,columnDefinition = "TEXT")
    private String question;

    @Column(name="student_answer",nullable = false,columnDefinition = "TEXT")
    private String studentAnswer;

    @Column(name="reference_answer",nullable = false,columnDefinition = "TEXT")
    private String referenceAnswer;

    @Column(name="score",nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name="feedback",columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "bloom_level",length = 20,nullable = false)
    private String bloomLevel;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}

