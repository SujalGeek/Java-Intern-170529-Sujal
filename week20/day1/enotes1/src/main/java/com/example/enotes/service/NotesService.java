package com.example.enotes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.FavouriteNoteDto;
import com.example.enotes.dto.NotesDto;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.handler.NotesResponse;

@Service
public interface NotesService {

	
	public Boolean saveNotes(String notes,MultipartFile file) throws Exception;
	
	public 	List<NotesDto> getAllNotes();
	
	public byte[] downloadFile(FileDetails fileDetails) throws Exception;
	
	public FileDetails getFileDetails(Integer id) throws Exception;
	
	public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize);
	
	public void softDeleteNotes(Integer id) throws Exception;
	
	public void restoreNotes(Integer id) throws Exception;
	
	public List<NotesDto> getUserRecycleBinNotes(Integer userId);
	
	public void hardDeleteNotes(Integer id) throws Exception;
	
	public void emptyRecycleBin(int userId);
	
	public void favouriteNotes(Integer noteId) throws Exception;

	public void unfavouriteNotes(Integer noteId) throws Exception;
	
	public List<FavouriteNoteDto> getUserFavouritesNotes() throws Exception;
	
	public Boolean copyNotes(Integer id) throws Exception;
}
