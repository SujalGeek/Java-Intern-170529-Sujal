package com.example.enotes.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.enotes.dto.CategoryDto;
import com.example.enotes.exception.ValidationException;

@Component
public class Validation {

	public void categoryValidation(CategoryDto categoryDto)
	{
		Map<String,Object> error = new LinkedHashMap<>();
		
		if(ObjectUtils.isEmpty(error))
		{
			throw new IllegalArgumentException("Catgeory Object/JSON should not be null");
		}
		
		
		if(ObjectUtils.isEmpty(categoryDto.getName()))
		{
			error.put("name", "name field should not be empty or null");
		}
		else {
			if(categoryDto.getName().length() <10)
			{
				error.put("name", "name length be 10");
			}
			if(categoryDto.getName().length()> 100)
			{
				error.put("name", "name length max be 100");
			}
		
		if(ObjectUtils.isEmpty(categoryDto.getDescription()))
		{
			error.put("description", "Description field is empty or null");
		}
		
		if(ObjectUtils.isEmpty(categoryDto.getIsActive()))
		{
			error.put("isActive", "isActive field is empty or null!!");
		}
		else {
			if(categoryDto.getIsActive() != Boolean.TRUE.booleanValue()
					&& categoryDto.getIsActive() != Boolean.FALSE.booleanValue())
				{
				error.put("isActive", "Invalid Active field");
				}
			}
		}
		
		if(!error.isEmpty())
		{
			throw new ValidationException(error);
		}
	}
}
