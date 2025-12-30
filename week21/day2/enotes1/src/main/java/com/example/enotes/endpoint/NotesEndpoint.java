package com.example.enotes.endpoint;

import java.security.Principal;

import static com.example.enotes.util.Constants.ROLE_ADMIN;
import static com.example.enotes.util.Constants.ROLE_ADMIN_USER;
import static com.example.enotes.util.Constants.ROLE_USER;
import static com.example.enotes.util.Constants.DEFAULT_PAGE_NO;
import static com.example.enotes.util.Constants.DEFAULT_PAGE_SIZE;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Notes Management", description = "Core operations for creating, searching, and managing notes")
@RequestMapping("/api/v1/notes")
public interface NotesEndpoint {

    @Operation(summary = "Save Note", description = "Create or update a note with an optional file attachment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Note Saved Successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid Input"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
	@PostMapping("/save")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> saveNotes(
            @Parameter(description = "Note JSON string") @RequestParam String notes,
			@Parameter(description = "Optional file attachment") @RequestParam(required = false) MultipartFile file,
            Principal principal) throws Exception;
	
    
    @Operation(summary = "Get All Notes (Admin)", description = "Retrieve all notes in the system")
	@GetMapping("/")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getAllNotes();
	
    
    @Operation(summary = "Download File", description = "Download a file attached to a note")
	@GetMapping("/download/{id}")
	@PreAuthorize(ROLE_ADMIN_USER)
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Exception;
	
    
    @Operation(summary = "Get My Notes", description = "Retrieve paginated notes for the logged-in user")
	@GetMapping("/user-notes")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getAllNotesByUser(
            @RequestParam(name = "pageNo", defaultValue = DEFAULT_PAGE_NO) Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize, 
            Principal principal);
	
    
    @Operation(summary = "Soft Delete Note", description = "Move a note to the recycle bin")
	@GetMapping("/delete/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Exception;
	
    
    @Operation(summary = "Get Recycle Bin", description = "View notes in the recycle bin")
	@GetMapping("/recycle-bin")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getUserRecycleBinNotes(Principal principal) throws Exception;
	
    
    @Operation(summary = "Hard Delete Note", description = "Permanently delete a note from the system")
	@DeleteMapping("/delete/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Exception;
	
    
    @Operation(summary = "Restore Note", description = "Restore a note from the recycle bin")
	@GetMapping("/restore/{id}")
	@PreAuthorize(ROLE_USER)
    public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Exception;
	
	
    @Operation(summary = "Empty Recycle Bin", description = "Permanently delete all notes in the recycle bin")
	@DeleteMapping("/delete")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> emptyRecycleBin(Principal principal) throws Exception;
	
    
    @Operation(summary = "Add to Favorites", description = "Mark a note as favorite")
	@PostMapping("/fav/{noteId}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId, Principal principal) throws Exception;
	
    
    @Operation(summary = "Remove from Favorites", description = "Unmark a note as favorite")
	@DeleteMapping("/un-fav/{favouriteNoteId}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> unFavouriteNote(@PathVariable Integer favouriteNoteId) throws Exception;
	
    
    @Operation(summary = "Get Favorite Notes", description = "Retrieve all favorite notes")
	@GetMapping("/fav-note")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getUserFavouriteNote(Principal principal) throws Exception;
	
    
    @Operation(summary = "Copy Note", description = "Duplicate an existing note")
	@GetMapping("/copy/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> copyNotes(@PathVariable Integer id, Principal principal) throws Exception;
	
    
    @Operation(summary = "Search Notes", description = "Search for notes by keyword")
	@GetMapping("/search")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> searchNotes(
            @Parameter(description = "Search keyword") @RequestParam(name = "key", defaultValue = "") String key,
			@RequestParam(name = "pageNo", defaultValue = DEFAULT_PAGE_NO) Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
			Principal principal
			);
	
}