package com.example.enotes.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class FileDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "upload_file_name")
	private String uploadName;
	
	@Column(name = "original_file_name")
	private String originalFileName;
	
	@Column(name = "display_file_name")
	private String displayName;
	
	private String path;
	
	@Column(name = "file_size")
	private Long fileSize;
}
