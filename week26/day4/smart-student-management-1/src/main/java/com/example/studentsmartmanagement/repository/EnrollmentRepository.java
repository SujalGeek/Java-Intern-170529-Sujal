package com.example.studentsmartmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	List<Enrollment> findByStudent_StudentId(Long studentId);
	
	Optional<Enrollment> findByStudent_StudentIdAndCourse_CourseId(Long studentId, Long courseId);

	List<Enrollment> findByCourse_CourseId(Long courseId);
		
}
