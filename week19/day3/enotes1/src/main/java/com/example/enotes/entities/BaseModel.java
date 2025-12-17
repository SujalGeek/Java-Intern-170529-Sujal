package com.example.enotes.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseModel {

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
