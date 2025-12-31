package com.example.enotes.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.enotes.entities.Notes;

@Repository
public interface NotesRepository  extends JpaRepository<Notes, Integer>{

	Page<Notes> findByCreatedBy(Integer userId,Pageable pageable);
	
	List<Notes> findByCreatedByAndIsDeletedTrue(Integer userId);
	
	Page<Notes> findByCreatedByAndIsDeletedFalse(Integer userId,Pageable pageable);
	
	List<Notes> findAllByIsDeletedAndDeletedOnBefore(boolean b,LocalDateTime cutOffTime);
	
	
	@Query("""
			 SELECT n FROM Notes n
    WHERE (
        LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(n.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(n.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND n.isDeleted = false
    AND n.createdBy = :userId
			""")
	Page<Notes> searchNotes(@Param("keyword") String keyword,@Param("userId") Integer userId, Pageable pageable); 
}
