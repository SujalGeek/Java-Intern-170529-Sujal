package com.example.rest.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rest.api.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{

}
