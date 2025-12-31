package com.example.enotes.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.TodoDto;
import com.example.enotes.endpoint.TodoEndpoint;
import com.example.enotes.entities.User;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.TodoService;
import com.example.enotes.util.CommonUtil;

@RestController
public class TodoController implements TodoEndpoint {
	
	@Autowired
	private TodoService todoService;
	
	@Autowired
	private UserRepository userRepository;
	
	// Helper method to get ID from Token
	private Integer getUserFromPrincipal(Principal principal) {
		String email = principal.getName();
		User user = userRepository.findByEmail(email);
		return user.getId();
	}

	@Override
	public ResponseEntity<?> savedTodo(@RequestBody TodoDto todo, Principal principal) throws Exception {
		// 1. Get the User ID
		Integer userId = getUserFromPrincipal(principal);
		
		// 2. Pass it to the service
		Boolean savedTodo = todoService.saveTodo(todo, userId);
		
		if(savedTodo) {
			return CommonUtil.createBuildResponseMessage("Todo Saved Success", HttpStatus.CREATED);
		} else {
			return CommonUtil.createErrorResponseMessage("Todo not Saved!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<?> getTodo(@PathVariable Integer id) throws Exception {
		TodoDto todo = todoService.getTodoById(id);
		return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
	}
	
	@Override
	// 🚨 FIX: Added Principal here to identify the user
	public ResponseEntity<?> getAllTodoByUser(Principal principal) throws Exception {
		
		// 1. Get User ID from Token
		Integer userId = getUserFromPrincipal(principal);
		
		// 2. 🚨 FIX: Pass userId to the service (Service now expects an Integer)
		List<TodoDto> todoList = todoService.getTododByUser(userId);
		
		if(CollectionUtils.isEmpty(todoList)) {
			return ResponseEntity.noContent().build();
		} else {
			return CommonUtil.createBuildResponse(todoList, HttpStatus.OK);
		}
	}
}