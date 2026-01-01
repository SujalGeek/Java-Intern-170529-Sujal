package com.example.enotes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enotes.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	Boolean existsByName(String name);

	List<Category> findByIsActiveTrueAndIsDeletedFalse();

	Optional<Category> findByIdAndIsDeletedFalse(Integer id);

	List<Category> findByIsDeletedFalse();
}
