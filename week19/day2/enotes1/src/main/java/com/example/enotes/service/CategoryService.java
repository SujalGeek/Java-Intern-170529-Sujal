package com.example.enotes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enotes.dto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.CategoryResponse;

@Service
public interface CategoryService {

	public Boolean saveCategory(CategoryDto category);

	public List<CategoryDto> getAllCategory();
	
	public List<CategoryResponse> getActiveCategory();
}
