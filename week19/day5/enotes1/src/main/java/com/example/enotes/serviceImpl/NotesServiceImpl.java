package com.example.enotes.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.NotesDto;
import com.example.enotes.dto.NotesDto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.entities.Notes;
import com.example.enotes.exception.ResourceNotFoundException;
import com.example.enotes.handler.NotesResponse;
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
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Override
    public Boolean saveNotes(String notes,MultipartFile file) throws IOException, ResourceNotFoundException {
    	
    	ObjectMapper ob = new ObjectMapper();
    	NotesDto notesDto = ob.readValue(notes,NotesDto.class);
    	
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
    		notesMap.setUpdatedBy(1);
    		notesMap.setUpdatedOn(new Date());
    	}
    	else {
    		notesMap.setCreatedBy(1);
    		notesMap.setCreatedOn(new Date());
    		notesMap.setUpdatedBy(1);
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
     if(!ObjectUtils.isEmpty(saveNotes))
     {
    	 return true;
     }
     else {
    	 return false;
     }
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
    		
    		// upload file
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
    	// TODO Auto-generated method stub
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
		FileDetails fileDetails = fileRepository.findById(id)
				.orElseThrow(
					() -> new ResourceNotFoundException("File is not available"));
		return fileDetails;
	}
	@Override
	public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Notes> pagesNotes = notesRepository.findByCreatedBy(userId, pageable);
		
		List<NotesDto> notesDto = pagesNotes.map(n-> modelMapper.map(n,NotesDto.class)).toList();
		
//		NotesResponse notes = NotesResponse.builder().notes(notesDto).pageNo
		
		NotesResponse notes = NotesResponse.builder()
				.notes(notesDto)
				.pageNo(pagesNotes.getNumber())
				.pageSize(pagesNotes.getSize()) // <--- ADD THIS (populates the new field)
				.totalPages(pagesNotes.getTotalPages())
				.totalElements(pagesNotes.getTotalElements())
				.isFirst(pagesNotes.isFirst())
				.isLast(pagesNotes.isLast())
				.build();
		
		return notes;
	}
}