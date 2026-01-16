package com.example.studentsmartmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

	List<Attendance> findByEnrollment_EnrollmentId(Long enrollmentId);	
}
