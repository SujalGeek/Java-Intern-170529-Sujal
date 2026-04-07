package com.example.ai_integration_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class IndexController {

    // 🔥 FIX: Inject the specific bean name you defined in RestTemplateConfig
    @Autowired
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; 
    
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;

    public String getIndexUrl() {
        return nlpServiceUrl + "/index-book";
    }

    @PostMapping("/{courseId}/index") // 🔥 Added courseId to path
    public ResponseEntity<?> indexBook(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId, // 🔥 Capture the ID
            @RequestParam("file") MultipartFile file) {
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // 🔥 THE MISSING LINK: Python needs "course_id"
            body.add("course_id", courseId.toString()); 
            
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Use your externalRestTemplate
            ResponseEntity<String> response = restTemplate.postForEntity(
                    getIndexUrl(), 
                    requestEntity, 
                    String.class);

            return ResponseEntity.ok(response.getBody());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Indexing failed: " + e.getMessage());        
        }
    }
}