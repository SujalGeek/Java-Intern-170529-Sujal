package com.example.enotes.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseModel {
	
    // DISABLED: We set this manually in Service
    // @CreatedBy
	@Column(updatable = false)
	private Integer createdBy;
	
    // DISABLED: We set "new Date()" manually in Service
    // @CreationTimestamp
	@Column(updatable = false)
	private Date createdOn;
	
    // DISABLED: We set this manually in Service
    // @LastModifiedBy
	@Column(insertable = false)
	private Integer updatedBy;
	
    // DISABLED: We set "new Date()" manually in Service
    // @UpdateTimestamp
	@Column(insertable = false)
	private Date updatedOn;
}