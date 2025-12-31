package com.example.enotes.controller;

import java.security.Principal;
import java.util.List;

import static com.example.enotes.util.Constants.DEFAULT_PAGE_NO;
import static com.example.enotes.util.Constants.DEFAULT_PAGE_SIZE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.enotes.dto.FavouriteNoteDto;
import com.example.enotes.dto.NotesDto;
import com.example.enotes.endpoint.NotesEndpoint;
import com.example.enotes.entities.FileDetails;
import com.example.enotes.entities.User;
import com.example.enotes.handler.NotesResponse;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.NotesService;
import com.example.enotes.util.CommonUtil;

@RestController
public class NotesController implements NotesEndpoint {

	@Autowired
	private NotesService notesService;
	
	@Autowired
	private UserRepository userRepository;
	
	//  Helper Method 
	private Integer getUserIdFromPrincipal(Principal principal) {
		String email = principal.getName();
		User user = userRepository.findByEmail(email);
		return user.getId();
	}

	@Override
	public ResponseEntity<?> saveNotes(@RequestParam String notes,
			@RequestParam(required = false) MultipartFile file, Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		Boolean saveNotes = notesService.saveNotes(notes, file, userId);
		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	    System.out.println("================ DEBUG START ================");
//	    System.out.println("User Email: " + auth.getName());
//	    System.out.println("Authorities (What Spring Sees): " + auth.getAuthorities());
//	    System.out.println("================ DEBUG END ==================");
		
		if(saveNotes) {
			return CommonUtil.createBuildResponseMessage("Notes Saved Success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<?> getAllNotes() {
		List<NotesDto> notes = notesService.getAllNotes();
		if(CollectionUtils.isEmpty(notes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception {
		FileDetails fileDetails = notesService.getFileDetails(id);
		byte[] data = notesService.downloadFile(fileDetails);
		
		HttpHeaders headers = new HttpHeaders();
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());
		
		return ResponseEntity.ok().headers(headers).body(data);
	}
	
	@Override
	public ResponseEntity<?> getAllNotesByUser(
			@RequestParam(name = "pageNo", defaultValue = DEFAULT_PAGE_NO ) Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize, 
			Principal principal) {
		Integer userId = getUserIdFromPrincipal(principal);
		NotesResponse notes = notesService.getAllNotesByUser(userId, pageNo, pageSize);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception {
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getUserRecycleBinNotes(Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		List<NotesDto> notes = notesService.getUserRecycleBinNotes(userId);
		if(CollectionUtils.isEmpty(notes)) {
			return CommonUtil.createBuildResponseMessage("Notes not available in Recycle Bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception {
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception {
		notesService.restoreNotes(id);
		return CommonUtil.createBuildResponseMessage("Notes Restored Success", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> emptyRecycleBin(Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		notesService.emptyRecycleBin(userId);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId, Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		notesService.favouriteNotes(noteId, userId); 
		return CommonUtil.createBuildResponseMessage("Notes Added Favourite", HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> unFavouriteNote(@PathVariable Integer favouriteNoteId) throws Exception {
		notesService.unfavouriteNotes(favouriteNoteId);
		return CommonUtil.createBuildResponseMessage("Remove Favourite", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getUserFavouriteNote(Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		List<FavouriteNoteDto> userFavouritesNotes = notesService.getUserFavouritesNotes(userId);
		if(CollectionUtils.isEmpty(userFavouritesNotes)) {
			return ResponseEntity.noContent().build();	
		} else {
			return CommonUtil.createBuildResponse(userFavouritesNotes, HttpStatus.OK);
		}
	}
	
	@Override
	public ResponseEntity<?> copyNotes(@PathVariable Integer id, Principal principal) throws Exception {
		Integer userId = getUserIdFromPrincipal(principal);
		Boolean copyNotes = notesService.copyNotes(id, userId);
		if(copyNotes) {
			return CommonUtil.createBuildResponseMessage("Copied Successfully", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Copied Failed !! Try Again !!", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<?> searchNotes(
			@RequestParam(name = "key", defaultValue = "") String key,
			@RequestParam(name = "pageNo", defaultValue = DEFAULT_PAGE_NO) Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
			Principal principal) {
		
		//  Fixed: Uses helper method now, cleaner logic
		Integer userId = getUserIdFromPrincipal(principal);
		
		System.out.println("logged in with user id " + userId);
		System.out.println("Searching for the key: " + key);
		
		NotesResponse notes = notesService.getNotesByUserSearch(userId, pageNo, pageSize, key);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
}