package com.example.enotes.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.example.enotes.dto.CategoryDto;
import com.example.enotes.dto.TodoDto;
import com.example.enotes.dto.TodoDto.StatusDto;
import com.example.enotes.dto.UserDto;
import com.example.enotes.dto.UserDto.RoleDto;
import com.example.enotes.entities.Role;
import com.example.enotes.enums.TodoStatus;
import com.example.enotes.exception.ExistDataException;
import com.example.enotes.exception.ResourceNotFoundException;
import com.example.enotes.exception.ValidationException;
import com.example.enotes.repository.RoleRepository;
import com.example.enotes.repository.UserRepository;



@Component
public class Validation {

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public void categoryValidation(CategoryDto categoryDto) {

		Map<String, Object> error = new LinkedHashMap<>();

		
		if (ObjectUtils.isEmpty(categoryDto)) {
			throw new IllegalArgumentException("category Object/JSON shouldn't be null or empty");
		} else {

			// validation name field
			if (ObjectUtils.isEmpty(categoryDto.getName())) {
				error.put("name", "name field is empty or null");
			} else {
				if (categoryDto.getName().length() < 3) {
					error.put("name", "name length min 3");
				}
				if (categoryDto.getName().length() > 100) {
					error.put("name", "name length max 100");
				}
			}

			// validation dscription
			if (ObjectUtils.isEmpty(categoryDto.getDescription())) {
				error.put("description", "description field is empty or null");
			}

			// validation isActive
			if (ObjectUtils.isEmpty(categoryDto.getIsActive())) {
				error.put("isActive", "isActive field is empty or null");
			} else {
				if (categoryDto.getIsActive() != Boolean.TRUE.booleanValue()
						&& categoryDto.getIsActive() != Boolean.FALSE.booleanValue()) {
					error.put("isActive", "invalid value isActive field ");
				}
			}
		}

		if (!error.isEmpty()) {
			throw new ValidationException(error);
		}

	}
	public void todoValidation(TodoDto todo) throws Exception{
		StatusDto reqStatus = todo.getStatus();
		Boolean statusFound = false;
		for(TodoStatus st: TodoStatus.values())
		{
			if(st.getId().equals(reqStatus.getId()))
			{
				statusFound=true;
			}
		}
		if(!statusFound)
		{
			throw new ResourceNotFoundException("Invalid Status");
		}
	}
	
	public void userValidation(UserDto userDto)
	{
		Map<String, Object> error = new LinkedHashMap<>();
		
		
		if(!StringUtils.hasText(userDto.getFirstName()))
		{
			error.put("firstName", "First Name is required");
		}
		else if (userDto.getFirstName().length() < 3 || userDto.getFirstName().length() > 50) {
            error.put("firstName", "First Name must be between 3 and 50 characters");
       }
		
		if(!StringUtils.hasText(userDto.getLastName()))
		{
			error.put("lastName", "Last Name is required");
		}
		
		if(!StringUtils.hasText(userDto.getEmail()))
		{
			error.put("email", "Email is required");
		} else if (!userDto.getEmail().matches(Constants.EMAIL_REGREX)) {
            error.put("email", "Invalid Email format");
        }
		else {
			Boolean existEmail = userRepository.existsByEmail(userDto.getEmail());
			if (existEmail) {
				error.put("email", "Email already exists");
			}
		}
		
		if (!StringUtils.hasText(userDto.getMobNo())) {
            error.put("mobNo", "Mobile Number is required");
        } else if (!userDto.getMobNo().matches(Constants.MOBNO_REGREX)) {
            error.put("mobNo", "Invalid Mobile Number");
        }
		
		if(CollectionUtils.isEmpty(userDto.getRoles()))
		{
			error.put("roles", "At least one Role is required");
		}
		else {
			List<Integer> reqRoleIds = userDto.getRoles().stream()
					.map(RoleDto::getId)
					.collect(Collectors.toList());
		
			List<Role> validRole = roleRepository.findAllById(reqRoleIds);
			
			if(validRole.size() != reqRoleIds.size())
			{
				error.put("roles", "One or more Role IDs are invalid");
			}
		}
		
		if (!error.isEmpty()) {
			throw new ValidationException(error);
		}
	}
}