package com.example.enotes.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import com.example.enotes.util.Validation;

@Service
public class CategoryServiceImpl implements CategoryService{


	@Autowired
	private CategoryRepository categoryRepository;

	
	@Autowired
	private ModelMapper modelMapper;
    
	@Autowired
	private Validation validation;
	
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
		
		validation.categoryValidation(categoryDto);
		Category category1 = modelMapper.map(categoryDto, Category.class);
		
		if(ObjectUtils.isEmpty(category1.getId()))
		{
			category1.setIsDeleted(false);
			category1.setCreatedBy(1);
			category1.setUpdatedBy(1); // You must set this because DB column is NOT NULL
			category1.setCreatedOn(new Date());	
			
		}
		else {
			updateCategory(category1);
		}
		
		Category savedCategory = categoryRepository.save(category1);	
		if(ObjectUtils.isEmpty(savedCategory))
		{
			return false;
		}
		return true;
	}

	private void updateCategory(Category category1) {
		Optional<Category> findById = categoryRepository.findById(category1.getId());
		if(findById.isPresent())
		{
			Category existCategory = findById.get();
			category1.setCreatedBy(existCategory.getCreatedBy());
			category1.setCreatedOn(existCategory.getCreatedOn());
			category1.setIsDeleted(existCategory.getIsDeleted());
			
			
			category1.setUpdatedBy(1);
			category1.setUpdatedOn(new Date());
		}
		
	}

	@Override
	public List<CategoryDto> getAllCategory() {
		List<Category> categories = categoryRepository.findAll();
		
		List<CategoryDto> categoryList = categories.stream().map(cat-> modelMapper.map(cat,CategoryDto.class)).toList();
		return categoryList;
	}

	@Override
	public List<CategoryResponse> getActiveCategory() {
		
		List<Category> categories = categoryRepository.findByIsActiveTrueAndIsDeletedFalse();
		List<CategoryResponse> categoryList = categories.stream().map(cat -> modelMapper.map(cat, CategoryResponse.class))
				.toList();
		return categoryList;
	}
	
	@Override
	public CategoryDto getCatgeoryById(Integer id)
	{
		Optional<Category> findByCategory = categoryRepository.findByIdAndIsDeletedFalse(id);
		
		if(findByCategory.isPresent())
		{
			Category category = findByCategory.get();
			return modelMapper.map(category,CategoryDto.class);
		}
		return null;
	}
	
	
	@Override
	public Boolean deleteCategory(Integer id)
	{
		Optional<Category> findByCategory = categoryRepository.findById(id);
		
		if(findByCategory.isPresent())
		{
			Category category = findByCategory.get();
			category.setIsDeleted(true);
			categoryRepository.save(category);
			return true;
		}
		return false;
	}

}
