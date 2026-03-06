package com.example.course_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.course_service.dto.EnrollmentDto;
import com.example.course_service.dto.EnrollmentResponseDTO;
import com.example.course_service.entity.Course;
import com.example.course_service.entity.Enrollment;
import com.example.course_service.repository.CourseRepository;
import com.example.course_service.repository.EnrollmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    // --- 🔥 1. THE MISSING METHOD FOR DASHBOARD (DTO MAPPING) ---
    // This is what the Student Dashboard calls to see their course list.
    public List<EnrollmentResponseDTO> getEnrollments(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        
        // We map to DTO to prevent Hibernate 500 Serialization Errors
        return enrollments.stream().map(enrollment -> {
            // Fetch the course details so the student sees the Name and Code, not just ID
            Course course = courseRepository.findById(enrollment.getCourseId()).orElse(null);
            
            return EnrollmentResponseDTO.builder()
                    .enrollmentId(enrollment.getEnrollmentId())
                    .courseId(enrollment.getCourseId())
                    .courseName(course != null ? course.getCourseName() : "Unknown Course")
                    .courseCode(course != null ? course.getCourseCode() : "N/A")
                    .instructorName(course != null ? "Prof. ID: " + course.getTeacherId() : "TBD")
                    .status(enrollment.getStatus().toString())
                    .enrollmentDate(LocalDateTime.now()) // Or map from entity if you have it
                    .progress(75.0) // Mock progress for the UI progress bar
                    .build();
        }).collect(Collectors.toList());
    }

    // --- 2. GET RAW ENROLLMENTS (REWRITTEN TO BE SAFE) ---
    public List<Enrollment> getStudentEnrollment(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        // Senior Tip: Don't throw RuntimeException here, let the Controller handle empty []
        return enrollments;
    }

    // --- 3. ENROLLMENT LOGIC (UNCHANGED BUT CLEANED) ---
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new RuntimeException("Cannot enroll: Course ID " + courseId));
        
        if(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new RuntimeException("Student is already enrolled in this course");
        }
        
        long activeEnrollments = enrollmentRepository.countByStudentIdAndStatus(studentId, Enrollment.Status.ACTIVE);
        if(activeEnrollments >= 3) {
            throw new RuntimeException("Enrollment Limit Reached (Max 3 courses).");
        }
        
        long currentClassSize = enrollmentRepository.countByCourseIdAndStatus(courseId, Enrollment.Status.ACTIVE);
        if(currentClassSize >= course.getMaxStudents()) {
            throw new RuntimeException("Course Full: Capacity is " + course.getMaxStudents());
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus(Enrollment.Status.ACTIVE);
        // Note: Make sure your Enrollment entity has @CreatedDate or set it here
        return enrollmentRepository.save(enrollment);
    }

    // --- 4. STATUS UPDATE ---
    public Enrollment updateStatus(Long enrollmentId, Enrollment.Status status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment Id: " + enrollmentId + " not found"));
        
        enrollment.setStatus(status);
        return enrollmentRepository.save(enrollment);
    }

    // --- 5. DROP COURSE (SOFT DELETE) ---
    @Transactional
    public void dropCourse(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setStatus(Enrollment.Status.DROPPED);
        enrollmentRepository.save(enrollment);
    }
}