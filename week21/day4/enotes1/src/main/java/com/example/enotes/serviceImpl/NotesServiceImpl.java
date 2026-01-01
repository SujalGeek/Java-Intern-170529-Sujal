package com.example.enotes.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.FavouriteNote;
import com.example.enotes.dto.FavouriteNoteDto;
import com.example.enotes.dto.NotesDto;
import com.example.enotes.dto.NotesDto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.entities.Notes;
import com.example.enotes.exception.ResourceNotFoundException;
import com.example.enotes.handler.NotesResponse;
import com.example.enotes.repository.CategoryRepository;
import com.example.enotes.repository.FavouriteRepository;
import com.example.enotes.repository.FileRepository;
import com.example.enotes.repository.NotesRepository;
import com.example.enotes.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotesServiceImpl implements NotesService {

    @Autowired
    private NotesRepository notesRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileRepository fileRepository;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Override
    public Boolean saveNotes(String notes, MultipartFile file, Integer userId) throws IOException, ResourceNotFoundException {
        
        ObjectMapper ob = new ObjectMapper();
        NotesDto notesDto = ob.readValue(notes, NotesDto.class);
        
        Notes notesMap = modelMapper.map(notesDto, Notes.class);

        Category catgeory = checkCategoryExist(notesDto.getCategory());
        notesMap.setCategory(catgeory);
            
        if(notesMap.getId() != null)
        {
            Notes existingNote = notesRepository.findById(notesMap.getId()).orElse(null);
            
            if(existingNote != null)
            {
                notesMap.setCreatedBy(existingNote.getCreatedBy());
                notesMap.setCreatedOn(existingNote.getCreatedOn());
                notesMap.setFileDetails(existingNote.getFileDetails());
            }
            notesMap.setUpdatedBy(userId);
            notesMap.setUpdatedOn(new Date());
        }
        else {
            notesMap.setCreatedBy(userId);
            notesMap.setCreatedOn(new Date());
            notesMap.setUpdatedBy(userId);
            notesMap.setIsDeleted(false);
        }
        
        FileDetails fileDetails = saveFileDetails(file);
        if(!ObjectUtils.isEmpty(fileDetails))
        {
            notesMap.setFileDetails(fileDetails);
        }
        else {
            if(notesMap.getId() == null)
            {
                notesMap.setFileDetails(null);
            }
        }
        
        Notes saveNotes = notesRepository.save(notesMap);
        return !ObjectUtils.isEmpty(saveNotes);
    }

    private FileDetails saveFileDetails(MultipartFile file) throws IOException {
    
        if(!ObjectUtils.isEmpty(file) && !file.isEmpty())
        {
            String originalFileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFileName);
            
            List<String> extensionAllow = Arrays.asList("pdf","xlsx","jpg","png");
            if(!extensionAllow.contains(extension))
            {
                throw new IllegalArgumentException("Invalid File Format ! Upload only .pdf , .xlsx, jpg,png");
            }
            
            String endString = UUID.randomUUID().toString();
            String uploadFileName = endString+"."+extension;
        
            File saveFile = new File(uploadPath);
            if(!saveFile.exists())
            {
                saveFile.mkdirs();
            }
            String storePath = uploadPath + File.separator + uploadFileName;
            
            long upload = Files.copy(file.getInputStream(), Paths.get(storePath));
            if(upload != 0)
            {
                FileDetails fileDetails = new FileDetails();
                fileDetails.setOriginalFileName(originalFileName);
                fileDetails.setDisplayName(getDisplayName(originalFileName));
                fileDetails.setUploadName(uploadFileName);
                fileDetails.setFileSize(file.getSize());
                fileDetails.setPath(storePath);
                FileDetails saveFileDtls = fileRepository.save(fileDetails);
                return saveFileDtls;
            }
        }
        return null;
    }

    private Category checkCategoryExist(CategoryDto categoryDto) throws ResourceNotFoundException {
        return categoryRepository.findById(categoryDto.getId()).orElseThrow(
        () -> new ResourceNotFoundException("Category is Invalid"));
    }
    
    private String getDisplayName(String originalFileName)
    {
        String extension = FilenameUtils.getExtension(originalFileName);
        String fileName = FilenameUtils.removeExtension(originalFileName);
        
        if(fileName.length()>8)
        {
            fileName=fileName.substring(0,7);
        }
        fileName = fileName +"."+extension;
        return fileName;
    }

    @Override
    public List<NotesDto> getAllNotes() {
        return notesRepository.findAll().stream()
                .map(note -> modelMapper.map(note, NotesDto.class))
                .toList();
    }

    @Override
    public byte[] downloadFile(FileDetails fileDetails) throws Exception {
        try (InputStream io = new FileInputStream(fileDetails.getPath())) {
            return StreamUtils.copyToByteArray(io);
        }
    }

    @Override
    public FileDetails getFileDetails(Integer id) throws Exception {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File is not available"));
    }

    @Override
    public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Notes> pagesNotes = notesRepository.findByCreatedByAndIsDeletedFalse(userId, pageable);
        
        List<NotesDto> notesDto = pagesNotes.map(n-> modelMapper.map(n,NotesDto.class)).toList();
        
        return NotesResponse.builder()
                .notes(notesDto)
                .pageNo(pagesNotes.getNumber())
                .pageSize(pagesNotes.getSize())
                .totalPages(pagesNotes.getTotalPages())
                .totalElements(pagesNotes.getTotalElements())
                .isFirst(pagesNotes.isFirst())
                .isLast(pagesNotes.isLast())
                .build();
    }

    @Override
    public void softDeleteNotes(Integer id) throws Exception {
        Notes notes = notesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notes id invaid ! Not found"));
        notes.setIsDeleted(true);
        notes.setDeletedOn(LocalDateTime.now());
        notesRepository.save(notes);
    }

    @Override
    public void restoreNotes(Integer id) throws Exception {
        Notes notes = notesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notes id invalid ! Not found"));
        notes.setIsDeleted(false);
        notes.setDeletedOn(null);
        notesRepository.save(notes);
    }

    @Override
    public List<NotesDto> getUserRecycleBinNotes(Integer userId) {
        List<Notes> recycleNotes = notesRepository.findByCreatedByAndIsDeletedTrue(userId);
        return recycleNotes.stream().map(note->modelMapper.map(note,NotesDto.class)).toList();
    }

    @Override
    public void hardDeleteNotes(Integer id) throws Exception {
        Notes notes = notesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notes not found"));
        if(notes.getIsDeleted())
        {
            notesRepository.delete(notes);
        }
        else {
            throw new IllegalArgumentException("Sorry You can't hard delete Directly");
        }
    }

    @Override
    public void emptyRecycleBin(int userId) {
        List<Notes> recycleNotes = notesRepository.findByCreatedByAndIsDeletedTrue(userId);
        if (!CollectionUtils.isEmpty(recycleNotes)) {
            notesRepository.deleteAll(recycleNotes); 
        }
    }

    @Override
    public void favouriteNotes(Integer noteId, Integer userId) throws Exception { 
        Notes notes = notesRepository.findById(noteId).orElseThrow(
                () -> new ResourceNotFoundException("Notes not found & Id invalid!!"));
        
        FavouriteNote favNotes = FavouriteNote.builder()
                .userId(userId)
                .note(notes)
                .build();
        favouriteRepository.save(favNotes);
    }

    @Override
    public void unfavouriteNotes(Integer favouriteNoteId) throws Exception {
        FavouriteNote favNote = favouriteRepository.findById(favouriteNoteId).orElseThrow(
                () -> new ResourceNotFoundException("Favourite Note Not found & id invalid!!"));
        favouriteRepository.delete(favNote);
    }

    @Override
    public List<FavouriteNoteDto> getUserFavouritesNotes(Integer userId) throws Exception {
        List<FavouriteNote> favouritesNote = favouriteRepository.findByUserId(userId);
        return favouritesNote.stream().map(fn -> modelMapper.map(fn, FavouriteNoteDto.class)).toList();
    }

    @Override
    public Boolean copyNotes(Integer id, Integer userId) throws Exception { 
        
        Notes notes = notesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notes id invalid!! Not found!"));
        
        // 1. Copy the main fields using Builder
        Notes copyNote = Notes.builder()
                .title(notes.getTitle())
                .description(notes.getDescription())
                .category(notes.getCategory())
                .isDeleted(false)
                .fileDetails(null)
                .build();

        // 2. Set the User ID manually (This avoids Builder errors)
        copyNote.setCreatedBy(userId);
        copyNote.setUpdatedBy(userId);
        copyNote.setCreatedOn(new Date());
        copyNote.setUpdatedOn(new Date());
        
        Notes saveCopyNote = notesRepository.save(copyNote);
        return !ObjectUtils.isEmpty(saveCopyNote);
    }

    @Override
    public NotesResponse getNotesByUserSearch(Integer userId, Integer pageNo, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        Page<Notes> pageNotes = notesRepository.searchNotes(keyword, userId, pageable);
        
        List<NotesDto> notesDto = pageNotes.get()
                .map(note -> modelMapper.map(note, NotesDto.class))
                .toList();
        
        return NotesResponse.builder()
                .notes(notesDto)
                .pageNo(pageNotes.getNumber())
                .pageSize(pageNotes.getSize())
                .totalElements(pageNotes.getTotalElements())
                .totalPages(pageNotes.getTotalPages())
                .isFirst(pageNotes.isFirst())
                .isLast(pageNotes.isLast())
                .build();
    }
}