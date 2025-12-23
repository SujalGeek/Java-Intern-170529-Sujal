package com.example.enotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.enotes.entities.FileDetails;

public interface FileRepository extends JpaRepository<FileDetails, Integer>{

}
