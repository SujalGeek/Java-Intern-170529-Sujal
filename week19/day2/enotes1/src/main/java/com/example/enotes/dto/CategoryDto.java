package com.example.enotes.dto;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
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
	
	private String name;
	
	private String description;

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

	

