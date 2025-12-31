package com.example.enotes.endpoint;

import static com.example.enotes.util.Constants.ROLE_ADMIN;
import static com.example.enotes.util.Constants.ROLE_ADMIN_USER;

import org.springframework.http.ResponseEntity; 
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.enotes.dto.CategoryDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Category Management",description = "Operations related to the Category creation, retrieval and deletion")
@RequestMapping("/api/v1/category")
public interface CategoryEndpoint {
	

	@Operation(summary = "Save or Update Category",description = "Create a new Category or update an Existing one(Admin Only)")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "201",description = "Category Saved/Updated SuccessFully"),
					@ApiResponse(responseCode = "400",description = "Validation Failed (Name/Description missing)"),
					@ApiResponse(responseCode = "403",description = "Forbidden(Admin Access Required)"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@PostMapping("/save-category")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> saveCategory(@Valid @RequestBody CategoryDto category);
	
	@Operation(summary = "Get All Categories",description = "Retrieve a list of all Categories, including inactive ones (Admin Only)")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Categories Retrieved SuccessFully"),
					@ApiResponse(responseCode = "204",description = "No Categories Found"),
					@ApiResponse(responseCode = "403",description = "Forbidden(Admin Access Required)"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@GetMapping("/")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getAllCategory();
	
	
	@Operation(summary = "Get Active Categories",description = "Retrieve Only Active Categories (Available to User and Admin)")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Active Categories Retrieved Successfully"),
					@ApiResponse(responseCode = "204",description = "No Active Categories Found"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
					})
	@GetMapping("/active")
	@PreAuthorize(ROLE_ADMIN_USER)
	public ResponseEntity<?> getActiveCategory();
	
	
	@Operation(summary = "Get Category By Id", description = "Retrieve specific category details by its unique ID(Admin Only)")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Category Found"),
					@ApiResponse(responseCode = "403",description = "Forbidden(Admin Access Required)"),
					@ApiResponse(responseCode = "404",description = "Category Not Found with the given ID"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
					})
	@GetMapping("/{id}")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> getCategoriesById(@PathVariable Integer id);
	
	
	@Operation(summary = "Delete Category",description = "Soft/Hard delete a category by its ID(Admin Only)")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Category Deleted SuccessFully"),
					@ApiResponse(responseCode = "403",description = "Forbidden(Admin Access Required)"),
					@ApiResponse(responseCode = "404",description = "Category Not Found For Deletion"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
					})
	@DeleteMapping("/{id}")
	@PreAuthorize(ROLE_ADMIN)
	public ResponseEntity<?> deleteByCategoryById(@PathVariable Integer id);
	
	

}
