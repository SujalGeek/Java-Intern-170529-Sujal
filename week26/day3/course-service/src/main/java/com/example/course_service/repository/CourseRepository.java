package com.example.course_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.course_service.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {


	List<Course> findByTeacherId(Long teacherId);
	
	boolean existsByCourseCode(String courseCode);
	

}
