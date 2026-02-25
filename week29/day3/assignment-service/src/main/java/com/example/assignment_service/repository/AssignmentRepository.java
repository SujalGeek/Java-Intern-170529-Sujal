package com.example.assignment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assignment_service.entity.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

}
