package com.example.studentsmartmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

	Optional<Course> findByCourseCode(String courseCode);
	
	List<Course> findByTeacher_TeacherId(Long teacherId);
	
	
}
