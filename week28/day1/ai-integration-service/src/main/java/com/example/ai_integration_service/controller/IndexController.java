package com.example.ai_integration_service.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IndexController {

	private final RestTemplate restTemplate;
	
	private final String NLP_INDEX_URL = "http://localhost:5001/index-book";
	
	@PostMapping("/index-book")
	public ResponseEntity<?> indexBook(@RequestParam("file") MultipartFile file)
	{
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			
			ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
		}
	};
	
	body.add("file", fileResource);
	
	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,headers);
	
	ResponseEntity<String> response = restTemplate.postForEntity(
			NLP_INDEX_URL
			, requestEntity, String.class);
	
	return ResponseEntity.ok(response.getBody());
	
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Indexing failed: " + e.getMessage());		
			
		}
	}
}
