package com.example.enotes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.enotes.dto.CategoryDto;
import com.example.enotes.entities.Category;
import com.example.enotes.exception.ExistDataException; // Ensure this matches your Exception package
import com.example.enotes.repository.CategoryRepository;
import com.example.enotes.serviceImpl.CategoryServiceImpl;
import com.example.enotes.util.Validation;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepo;

	@Mock
	private Validation validation;

	@Mock
	private ModelMapper mapper;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	private CategoryDto categoryDto;
	private Category category;
	private List<Category> categories = new ArrayList<>();
	private List<CategoryDto> categoriesDto = new ArrayList<>();

	@BeforeEach
	public void initalize() {
		// Prepare Dummy Data
		categoryDto = CategoryDto.builder()
				.Id(null)
				.name("Java Notes")
				.description("java notes")
				.isActive(true)
				.build();

		category = Category.builder()
				.id(null)
				.name("Java Notes")
				.description("java notes")
				.isActive(true)
				.isDeleted(false)
				.build();

		categories.clear(); 
		categories.add(category);
		
		categoriesDto.clear();
		categoriesDto.add(categoryDto);
	}

	@Test
	public void testSaveCategory() {
		// Arrange
		when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(false);
		when(mapper.map(categoryDto, Category.class)).thenReturn(category);
		when(categoryRepo.save(category)).thenReturn(category);

		// Act
		Boolean saveCategory = categoryService.saveCategory(categoryDto);

		// Assert
		assertTrue(saveCategory);

		// Verify
		verify(validation).categoryValidation(categoryDto);
		verify(categoryRepo).existsByName(categoryDto.getName());
		verify(categoryRepo).save(category);
	}

	@Test
	public void testCategoryExist() {
		// Arrange
		when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(true);

		// Act & Assert
		ExistDataException exception = assertThrows(ExistDataException.class, () -> {
			categoryService.saveCategory(categoryDto);
		});

		assertEquals("Category already exist", exception.getMessage());
		verify(validation).categoryValidation(categoryDto);
		verify(categoryRepo).existsByName(categoryDto.getName());
		verify(categoryRepo, never()).save(category);
	}

	@Test
	public void testUpdateCategory() {
		// Arrange
		categoryDto.setId(1);
		category.setId(1);

		when(categoryRepo.existsByName(categoryDto.getName())).thenReturn(false);
		when(mapper.map(categoryDto, Category.class)).thenReturn(category);
		when(categoryRepo.save(category)).thenReturn(category);

		// Act
		Boolean saveCategory = categoryService.saveCategory(categoryDto);

		// Assert
		assertTrue(saveCategory);
		verify(validation).categoryValidation(categoryDto);
		verify(categoryRepo).save(category);
	}

	@Test
	public void testGetAllCategory() {
		// Arrange
		when(categoryRepo.findByIsDeletedFalse()).thenReturn(categories);
		
		// 👇 CRITICAL FIX: Teach the mock mapper what to do!
		when(mapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

		// Act
		List<CategoryDto> allCategory = categoryService.getAllCategory();

		// Assert
		assertEquals(categories.size(), allCategory.size());
		assertEquals("Java Notes", allCategory.get(0).getName()); // Verify content
		verify(categoryRepo).findByIsDeletedFalse();
	}
}