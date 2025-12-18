package com.example.enotes.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.NotesDto;
import com.example.enotes.dto.NotesDto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.entities.Notes;
import com.example.enotes.repository.CategoryRepository;
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
    private ModelMapper modelMapper;

    @Autowired
    private FileRepository fileRepository;
    
    @Override
    public Boolean saveNotes(String notes,MultipartFile file) {
    	
    	ObjectMapper ob = new ObjectMapper();
    	NotesDto notesDto = ob.readValue(notes,NotesDto.class);
    	
        
    	checkCategoryExist(notesDto.getCategoryDto());
    	
    	Notes notesMap = modelMapper.map(notesDto, Notes.class);
    
        FileDetails fileDetails = saveFileDetails(file);
//        if (notesDto.getCategoryDto() != null && notesDto.getCategoryDto().getId() != null) {
//            Category category = categoryRepository.findById(notesDto.getCategoryDto().getId())
//                    .orElseThrow(() -> new RuntimeException("Category not found")); // Or handle gracefully
//            notes.setCategory(category);
//        }

        // 3. Handle CreatedBy / UpdatedBy Logic
     if(!ObjectUtils.isEmpty(fileDetails))
     {
//    	 notesMap.set
     }
    }

    private void checkCategoryExist(CategoryDto categoryDto) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public List<NotesDto> getAllNotes() {
        return notesRepository.findAll().stream()
                .map(note -> modelMapper.map(note, NotesDto.class))
                .toList();
    }
}