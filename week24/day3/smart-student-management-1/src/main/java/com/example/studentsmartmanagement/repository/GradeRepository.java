package com.example.studentsmartmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

	List<Grade> findByEnrollment_EnrollmentId(Long enrollmentId);
}
