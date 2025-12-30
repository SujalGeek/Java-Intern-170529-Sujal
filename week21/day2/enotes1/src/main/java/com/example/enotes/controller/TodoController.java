package com.example.enotes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.TodoDto;
import com.example.enotes.endpoint.TodoEndpoint;
import com.example.enotes.service.TodoService;
import com.example.enotes.util.CommonUtil;

@RestController
public class TodoController implements TodoEndpoint{
	
	@Autowired
	private TodoService todoService;
	
	// FIX 1: Changed to @RequestBody
	@Override
	public ResponseEntity<?> savedTodo(@RequestBody TodoDto todo) throws Exception {
		Boolean savedTodo = todoService.saveTodo(todo);
		if(savedTodo) {
			return CommonUtil.createBuildResponseMessage("Todo Saved Success", HttpStatus.CREATED);
		} else {
			return CommonUtil.createErrorResponseMessage("Todo not Saved!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// FIX 2: Renamed method from 'saveTodo' to 'getTodo'
	@Override
	public ResponseEntity<?> getTodo(@PathVariable Integer id) throws Exception {
		TodoDto todo = todoService.getTodoById(id);
		return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getAllTodoByUser() throws Exception {
		List<TodoDto> todoList = todoService.getTododByUser();
		if(CollectionUtils.isEmpty(todoList)) {
			return ResponseEntity.noContent().build();
		} else {
			return CommonUtil.createBuildResponse(todoList, HttpStatus.OK);
		}
	}

}