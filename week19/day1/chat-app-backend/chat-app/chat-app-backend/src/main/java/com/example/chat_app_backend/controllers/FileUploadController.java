package com.example.chat_app_backend.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin("http://localhost:5173/")
public class FileUploadController {

	private final String UPLOAD_URI = System.getProperty("user.dir") +"/uploads/";
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file)
	{
		try {
			Path uploadPath = Paths.get(UPLOAD_URI);
			
		if(!Files.exists(uploadPath))
			{
				Files.createDirectories(uploadPath);
			}
		
		String filename = UUID.randomUUID().toString()+ "_" + file.getOriginalFilename();
		Path filePath = uploadPath.resolve(filename);
		
		Files.copy(file.getInputStream(), filePath,StandardCopyOption.REPLACE_EXISTING);
		
		String fileNameUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/images/")
				.path(filename)
				.toUriString();
		
		return ResponseEntity.ok(fileNameUrl);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("Upload failed");
		}
	}
}
