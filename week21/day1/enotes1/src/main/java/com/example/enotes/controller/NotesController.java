package com.example.enotes.controller;

import org.springframework.http.HttpHeaders;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.FavouriteNoteDto;
import com.example.enotes.dto.NotesDto;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.entities.User;
import com.example.enotes.handler.NotesResponse;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.NotesService;
import com.example.enotes.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

	@Autowired
	private NotesService notesService;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/save")
	public ResponseEntity<?> saveNotes(@RequestParam String notes,
			@RequestParam(required = false) MultipartFile file,Principal principal) throws Exception
	{
		Integer userId = getUserIdFromPrincipal(principal); // Get ID
		Boolean saveNotes = notesService.saveNotes(notes,file,userId);
		
		if(saveNotes)
		{
		return CommonUtil.createBuildResponseMessage("Notes Saved Success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/")
	public ResponseEntity<?> getAllNotes(){
		List<NotesDto> notes = notesService.getAllNotes();
	
		if(CollectionUtils.isEmpty(notes))
		{
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception
	{
		FileDetails fileDetails = notesService.getFileDetails(id);
		byte[] data = notesService.downloadFile(fileDetails);
		
		HttpHeaders headers = new HttpHeaders();
		
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());
		
		return ResponseEntity.ok().headers(headers).body(data);
	}
	
	@GetMapping("/user-notes")
	public ResponseEntity<?> getAllNotesByUser(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, Principal principal)
	{
		Integer userId = getUserIdFromPrincipal(principal); // Get ID
		NotesResponse notes = notesService.getAllNotesByUser(userId, pageNo, pageSize);
//		return notes;
//		if(CollectionUtils.isEmpty(notes))
//		{
//			return ResponseEntity.noContent().build();
//		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	
	@GetMapping("/delete/{id}")
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception{
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}
	
	@GetMapping("/recycle-bin")
	public ResponseEntity<?> getUserRecycleBinNotes(Principal principal) throws Exception{
		Integer userId = getUserIdFromPrincipal(principal); // Get ID
		List<NotesDto> notes = notesService.getUserRecycleBinNotes(userId);
		if(CollectionUtils.isEmpty(notes))
		{
			return CommonUtil.createBuildResponseMessage("Notes not available in Recycle Bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception{
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}
	

	@GetMapping("/restore/{id}")
    public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception {
        notesService.restoreNotes(id);
        return CommonUtil.createBuildResponseMessage("Notes Restored Success", HttpStatus.OK);
    }
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> emptyRecycleBin(Principal principal) throws Exception{
		Integer userId = getUserIdFromPrincipal(principal); // Get ID
		notesService.emptyRecycleBin(userId);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}
	
	@PostMapping("/fav/{noteId}")
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId,Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		notesService.favouriteNotes(noteId, userId); 
		return CommonUtil.createBuildResponseMessage("Notes Added Favourite", HttpStatus.CREATED);
	}

	private Integer getUserIdFromPrincipal(Principal principal) {
		String email = principal.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
	}

	@DeleteMapping("/un-fav/{favouriteNoteId}")
	public ResponseEntity<?> unFavouriteNote(@PathVariable Integer favouriteNoteId) throws Exception{
		notesService.unfavouriteNotes(favouriteNoteId);
		return CommonUtil.createBuildResponseMessage("Remove Favourite", HttpStatus.OK);
	}
	
	@GetMapping("/fav-note")
	public ResponseEntity<?> getUserFavouriteNote(Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal); // Get ID
		List<FavouriteNoteDto> userFavouritesNotes = notesService.getUserFavouritesNotes(userId);
		if(CollectionUtils.isEmpty(userFavouritesNotes))
		{
		return ResponseEntity.noContent().build();	
		}
		else {
			return CommonUtil.createBuildResponse(userFavouritesNotes, HttpStatus.OK);
		}
	}
	
	@GetMapping("/copy/{id}")
	public ResponseEntity<?> copyNotes(@PathVariable Integer id,Principal principal) throws Exception{
		Integer userId = getUserIdFromPrincipal(principal); // Your logic to get ID
		Boolean copyNotes = notesService.copyNotes(id, userId); // ✅ Pass userId
		if(copyNotes)
		{
			return CommonUtil.createBuildResponseMessage("Copied Successfully", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Copied Failed !! Try Again !!", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	@GetMapping("/search")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> searchNotes(@RequestParam (name = "key", defaultValue = "") String key,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			Principal principal
			)
	
	{
//		Integer userId =
		String username = principal.getName();
		User user = userRepository.findByEmail(username);
		Integer userId = user.getId();
		
	
		System.out.println("logged in with user id "+userId);
	    System.out.println("Searching for the key: "+key);
		
		NotesResponse notes = notesService.getNotesByUserSearch(userId, pageNo, pageSize, key);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}

}

