package com.example.enotes.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.FavouriteNoteDto;
import com.example.enotes.dto.NotesDto;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.handler.NotesResponse;

public interface NotesService {

    // ✅ Updated to accept userId
    public Boolean saveNotes(String notes, MultipartFile file, Integer userId) throws Exception;
    
    public List<NotesDto> getAllNotes();
    
    public byte[] downloadFile(FileDetails fileDetails) throws Exception;
    
    public FileDetails getFileDetails(Integer id) throws Exception;
    
    public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize);
    
    public NotesResponse getNotesByUserSearch(Integer userId, Integer pageNo, Integer pageSize, String keyword);
    
    public void softDeleteNotes(Integer id) throws Exception;
    
    public void restoreNotes(Integer id) throws Exception;
    
    public List<NotesDto> getUserRecycleBinNotes(Integer userId);
    
    public void hardDeleteNotes(Integer id) throws Exception;
    
    public void emptyRecycleBin(int userId);
    
    //  Updated to accept userId
    public void favouriteNotes(Integer noteId, Integer userId) throws Exception;

    public void unfavouriteNotes(Integer favouriteNoteId) throws Exception;
    
    // Updated to accept userId
    public List<FavouriteNoteDto> getUserFavouritesNotes(Integer userId) throws Exception;
    
    //  Updated to accept userId
    public Boolean copyNotes(Integer id, Integer userId) throws Exception;
}