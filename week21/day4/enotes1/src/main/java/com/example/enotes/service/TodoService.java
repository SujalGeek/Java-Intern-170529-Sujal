package com.example.enotes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enotes.dto.TodoDto;

@Service
public interface TodoService {

	public Boolean saveTodo(TodoDto todo,Integer userId) throws Exception;
	
	public TodoDto getTodoById(Integer id) throws Exception;
	
	public List<TodoDto> getTododByUser(Integer userId);
}
