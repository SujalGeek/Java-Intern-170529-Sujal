package com.example.enotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.enotes.entities.Notes;

@Repository
public interface NotesRepository  extends JpaRepository<Notes, Integer>{

}
