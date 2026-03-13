package com.example.performance_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.performance_service.entity.Performance;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>{

	Optional<Performance> findByStudentIdAndCourseId(Long studentId,Long courseId);

	List<Performance> findAllByStudentId(Long studentId);
}
