package com.example.enotes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enotes.dto.FavouriteNote;

@Repository
public interface FavouriteRepository extends JpaRepository<FavouriteNote, Integer>{

	List<FavouriteNote> findByUserId(int userId);
	
}
