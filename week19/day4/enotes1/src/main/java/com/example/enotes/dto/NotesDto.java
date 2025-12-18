package com.example.enotes.dto;

import java.util.Date; 

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotesDto {

	private Integer id;
	
	private String title;
	
	private String description;
	
	private CategoryDto categoryDto;
	
	private Integer createdBy;
	
	private Date createdOn;
	
	private Integer updatedBy;
	
	private Date updatedOn;
	
	private FilesDto fileDetails;
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public class CategoryDto{
		private Integer id;
		
		private String name;
	}
	
}
