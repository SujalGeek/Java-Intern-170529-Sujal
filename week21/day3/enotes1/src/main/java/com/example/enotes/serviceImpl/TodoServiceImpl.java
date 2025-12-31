package com.example.enotes.serviceImpl;

import java.util.Date;
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
public class TodoServiceImpl implements TodoService {
	
	@Autowired
	private TodoRepository todoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private Validation validation;
	

	@Override
	public Boolean saveTodo(TodoDto todoDto, Integer userId) throws Exception {
		
		validation.todoValidation(todoDto);
		
		// 1. Map DTO to Entity
		Todo todo = modelMapper.map(todoDto, Todo.class);
		todo.setStatusId(todoDto.getStatus().getId());
		
		// 2. 🚨 FIX: Set Audit Fields BEFORE Saving
		if (todoDto.getId() != null) {
			// Update Logic: Preserve original creation data
            // (Ideally, fetch from DB first, but for now we manually ensure updated fields)
			todo.setUpdatedBy(userId);
			todo.setUpdatedOn(new Date());
            
            // NOTE: If this is an update, you should theoretically fetch the existing 
            // 'createdBy' from DB so it doesn't become null. 
            // For now, assuming you handle valid IDs properly.
		} else {
			// Create Logic
			todo.setCreatedBy(userId);
			todo.setCreatedOn(new Date());
            todo.setIsDeleted(false); // Ensure this isn't null
		}

		// 3. Save to Repository (AFTER setting fields)
		Todo saveTodo = todoRepository.save(todo);
		
		return !ObjectUtils.isEmpty(saveTodo);
	}

	@Override
	public TodoDto getTodoById(Integer id) throws Exception {
		
		Todo todo = todoRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Todo Not Found!! id invalid!!!"));
		TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
		setStatus(todoDto, todo);
		return todoDto;
	}

	private void setStatus(TodoDto todoDto, Todo todo) {
		for(TodoStatus st: TodoStatus.values()) {
			if(st.getId().equals(todo.getStatusId())) {
				StatusDto statusDto = StatusDto.builder()
						.id(st.getId())
						.name(st.getName())
						.build();
				todoDto.setStatus(statusDto);
			}
		}
	}

	@Override
	// 4. 🚨 FIX: Removed hardcoded ID, added parameter
	public List<TodoDto> getTododByUser(Integer userId) { 
		List<Todo> todos = todoRepository.findByCreatedBy(userId);
		return todos.stream().map(td -> modelMapper.map(td, TodoDto.class)).toList();
	}

	
}