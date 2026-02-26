package com.example.course_service.entity;

import jakarta.persistence.*; // Technical Fix: javax -> jakarta
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "courses")
@Data 
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "teacher_id")
    private Long teacherId;

    // --- YOUR SPECIFIC LOGIC FIELDS ---
    @Column(name = "semester", length = 20)
    private Integer semester;

    @Column(name = "year")
    private Integer year;

    @Column(name = "max_students")
    private Integer maxStudents = 50; // Your default

    @Column(name = "credits")
    private Integer credits = 3; // Your default

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true; // Your default

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}