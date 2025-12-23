package com.example.enotes.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseModel {
	
	@CreatedBy
	@Column(updatable = false)
	private Integer createdBy;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Date createdOn;
	
	
	@LastModifiedBy
	@Column(insertable = false)
	private Integer updatedBy;
	
	@UpdateTimestamp
	private Date updatedOn;
}
