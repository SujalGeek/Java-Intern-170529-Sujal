package com.example.enotes.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.enotes.dto.TodoDto;
import com.example.enotes.dto.TodoDto.StatusDto;
import com.example.enotes.entities.Todo;
import com.example.enotes.enums.TodoStatus;
import com.example.enotes.exception.ResourceNotFoundException;
import com.example.enotes.repository.TodoRepository;
import com.example.enotes.service.TodoService;
import com.example.enotes.util.Validation;

@Service
public class TodoServiceImpl implements TodoService{
	
	@Autowired
	private TodoRepository todoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private Validation validation;
	

	@Override
	public Boolean saveTodo(TodoDto todoDto) throws Exception {
		
		validation.todoValidation(todoDto);
		
		Todo todo = modelMapper.map(todoDto, Todo.class);
		todo.setStatusId(todoDto.getStatus().getId());
		Todo saveTodo = todoRepository.save(todo);
		
		if(!ObjectUtils.isEmpty(saveTodo))
		{
			return true;
		}
		return false;
	}

	@Override
	public TodoDto getTodoById(Integer id) throws Exception {
		
		Todo todo = todoRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Todo Not Found!! id invalid!!!"));
		TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
		setStatus(todoDto,todo);
		return todoDto;
	}

	private void setStatus(TodoDto todoDto, Todo todo) {

		for(TodoStatus st: TodoStatus.values())
		{
			if(st.getId().equals(todo.getStatusId()))
			{
				StatusDto statusDto = StatusDto.builder()
						.id(st.getId())
						.name(st.getName())
						.build();
				todoDto.setStatus(statusDto);
			}
		}
	}

	@Override
	public List<TodoDto> getTododByUser() {
		Integer userId = 2;
		
		List<Todo> todos = todoRepository.findByCreatedBy(userId);
		return todos.stream().map(td-> modelMapper.map(td,TodoDto.class)).toList();
	}

}
