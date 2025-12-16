package com.example.enotes.serviceImpl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import com.example.enotes.Enotes1Application;
import com.example.enotes.dto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.CategoryResponse;
import com.example.enotes.repository.CategoryRepository;
import com.example.enotes.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService{


	@Autowired
	private CategoryRepository categoryRepository;

	
	@Autowired
	private ModelMapper modelMapper;
    
	@Override
	public Boolean saveCategory(CategoryDto categoryDto) {

//		Category category1 = new Category();
//		category1.setName(category.getName());
//		category1.setDescription(category.getDescription());
//		category1.setIsActive(category.getIsActive());
//		category1.setIsDeleted(false);
//		category1.setCreatedBy(1);
//		category1.setCreatedOn(new Date());
		//		category.setDeleted(false);
//		category.setCreatedBy(1);
//		category.setUpdatedBy(1);
		
		Category category1 = modelMapper.map(categoryDto, Category.class);
		
		Category savedCategory = categoryRepository.save(category1);	
		if(ObjectUtils.isEmpty(savedCategory))
		{
			return false;
		}
		return true;
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepository.findAll();
		
		List<CategoryDto> categoryList = categories.stream().map(cat-> modelMapper.map(cat,CategoryDto.class)).toList();
		return categoryList;
	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		
		List<Category> categories = categoryRepository.findByActiveTrueAndIsDeletedFalse();
		List<CategoryResponse> categoryList = categories.stream().map(cat -> modelMapper.map(cat, CategoryResponse.class))
				.toList();
		return categoryList;
	}

}
