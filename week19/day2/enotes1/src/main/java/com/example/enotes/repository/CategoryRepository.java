package com.example.enotes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enotes.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

	List<Category> findByActiveTrueAndIsDeletedFalse();

	
}
