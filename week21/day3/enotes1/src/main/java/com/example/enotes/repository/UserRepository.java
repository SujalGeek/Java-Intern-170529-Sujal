package com.example.enotes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enotes.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	Boolean existsByEmail(String email);
	
	User findByEmail(String email);
	
	Optional<User> findByStatus_ResetToken(String token);

}
