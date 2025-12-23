package com.example.enotes.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.TodoDto;
import com.example.enotes.service.TodoService;
import com.example.enotes.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {
	
	@Autowired
	private TodoService todoService;
	
	@PostMapping("/")
	public ResponseEntity<?> savedTodo(@RequestParam TodoDto todo) throws Exception{
		Boolean savedTodo = todoService.saveTodo(todo);
		if(savedTodo)
		{
			return CommonUtil.createBuildResponseMessage("Todo Saved Success", HttpStatus.CREATED);
		}
		else {
			return CommonUtil.createErrorResponseMessage("Todo not Saved!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> saveTodo(@PathVariable Integer id) throws Exception{
		TodoDto todo = todoService.getTodoById(id);
		return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> getAllTodoByUser() throws Exception{
		List<TodoDto> todoList = todoService.getTododByUser();
		if(CollectionUtils.isEmpty(todoList))
		{
			return ResponseEntity.noContent().build();
		}
		else {
			return CommonUtil.createBuildResponse(todoList, HttpStatus.OK);
		}
	}

}
