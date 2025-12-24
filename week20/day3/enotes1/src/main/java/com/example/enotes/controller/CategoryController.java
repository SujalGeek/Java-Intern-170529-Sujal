package com.example.enotes.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.entities.CategoryResponse;
import com.example.enotes.service.CategoryService;
import com.example.enotes.util.CommonUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;

	@PostMapping("/save-category")
	public ResponseEntity<?> saveCategory(@Valid @RequestBody CategoryDto category){
		Boolean savedCategory = categoryService.saveCategory(category);
		
		if(savedCategory)
		{
//		return new ResponseEntity<>("saved",HttpStatus.CREATED);
		return CommonUtil.createBuildResponseMessage("Saved Success", HttpStatus.CREATED);
		}
		else {
//			return new ResponseEntity<>("not saved",HttpStatus.INTERNAL_SERVER_ERROR);
		return CommonUtil.createErrorResponseMessage("Category Not Saved", HttpStatus.INTERNAL_SERVER_ERROR);	
		
		}
		
	}
	
	
	@GetMapping("/")
	public ResponseEntity<?> getAllCategory(){
		List<CategoryDto> allCategory = categoryService.getAllCategory();
		
		if(CollectionUtils.isEmpty(allCategory))
		{
			return ResponseEntity.noContent().build();
		}
		else {
//			return new ResponseEntity<>(allCategory,HttpStatus.OK);
			return CommonUtil.createBuildResponse(allCategory, HttpStatus.OK);
		}
	}
	
	@GetMapping("/active")
	public ResponseEntity<?> getActiveCategory(){
		List<CategoryResponse> activeCategories = categoryService.getActiveCategory();
		
		if(CollectionUtils.isEmpty(activeCategories))
		{
			return ResponseEntity.noContent().build();
		}
		else {
//			return new ResponseEntity<>(activeCategories, HttpStatus.OK);
			return CommonUtil.createBuildResponse(activeCategories, HttpStatus.OK);

		}
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCategoriesById(@PathVariable Integer id)
	{
		CategoryDto categoryDto = categoryService.getCatgeoryById(id);
		
		if(ObjectUtils.isEmpty(categoryDto))
		{
//			return new ResponseEntity<>("Category not found with id="+ id,HttpStatus.NOT_FOUND);
			return CommonUtil.createErrorResponseMessage("Internal Server Error", HttpStatus.NOT_FOUND);
		}
		
//		return new ResponseEntity<>(categoryDto,HttpStatus.OK);
		return CommonUtil.createBuildResponse(categoryDto, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteByCategoryById(@PathVariable Integer id)
	{
		Boolean deleted = categoryService.deleteCategory(id);
		
		if(deleted)
		{
//			return new ResponseEntity<>("Category deleted successfully",HttpStatus.OK);
			return CommonUtil.createBuildResponseMessage("Category Deleted Successfully", HttpStatus.OK);
		}
		else {
//			return new ResponseEntity<>("Category Not deleted",HttpStatus.INTERNAL_SERVER_ERROR);
			return CommonUtil.createErrorResponseMessage("Category Not Deleted Successfully", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
