package com.example.enotes.endpoint;

import org.springframework.http.ResponseEntity; 

import static com.example.enotes.util.Constants.ROLE_USER;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.enotes.dto.TodoDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Todo Management",description = "Operations for Managing user To-Do Lists")
@RequestMapping("/api/v1/todo")
public interface TodoEndpoint {

	
	@Operation(summary = "Create or Update Todo",description = "Saves a new Task or updates the existing task")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "201",description = "Todo Saved SuccessFully"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@PostMapping("/")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> savedTodo(@RequestBody TodoDto todo) throws Exception;
	
	@Operation(summary = "Get Todo By ID",description = "Retrieve a specific task by its ID")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Todo Found"),
					@ApiResponse(responseCode = "404",description = "Todo Not Found(or does not belong to user)"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@GetMapping("/{id}")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getTodo(@PathVariable Integer id) throws Exception;
	
	
	@Operation(summary = "Get All Todos",description = "Retrieve all tasks for the logged in user")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "List Retrieved SuccessFully"),
					@ApiResponse(responseCode = "204",description = "No Tasks Found"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@GetMapping("/list")
	@PreAuthorize(ROLE_USER)
	public ResponseEntity<?> getAllTodoByUser() throws Exception;
	
}
