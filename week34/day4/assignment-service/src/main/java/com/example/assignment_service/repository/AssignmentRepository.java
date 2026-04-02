package com.example.assignment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assignment_service.entity.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

	List<Assignment> findByCourseIdIn(List<Long> courseIds);
    
    // Existing helper for teacher course views
    List<Assignment> findByCourseId(Long courseId);
}
