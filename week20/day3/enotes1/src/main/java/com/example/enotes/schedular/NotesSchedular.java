package com.example.enotes.schedular;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.enotes.entities.Notes;
import com.example.enotes.repository.NotesRepository;

@Component
public class NotesSchedular {

	@Autowired
	private NotesRepository notesRepository;
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteNotesSchedular() {
		LocalDateTime cutOffDate = LocalDateTime.now().minusDays(7);
		List<Notes> deleteNotes = notesRepository.findAllByIsDeletedAndDeletedOnBefore(true, cutOffDate);
		notesRepository.deleteAll(deleteNotes);
	}
}
