package com.example.enotes.dto;


import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

	private Integer Id;
	
	@NotBlank(message = "name field should not be empty or null")
    @Size(min = 10, max = 100, message = "name length must be between 10 and 100")
	private String name;
	
	@NotBlank(message = "Description field is empty or null")
	private String description;

	@NotNull(message = "isActive field is empty or null")
	private Boolean isActive;
	
	private Boolean isDeleted;
	
	private Integer createdBy;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Date createdOn;
	
	private Integer updatedBy;
	
	@UpdateTimestamp
	private Date updatedOn;
}

	

