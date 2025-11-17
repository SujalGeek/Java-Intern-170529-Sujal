package com.example.revise_ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Value("${hf.api.key}")
    private String hfApiKey;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateImage(@RequestParam String prompt) {

        String url = "https://router.huggingface.co/hf-inference/models/stabilityai/stable-diffusion-xl-base-1.0";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hfApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.IMAGE_PNG));   //  FIX HERE

        // HuggingFace expects this JSON format:
        String body = "{ \"inputs\": \"" + prompt + "\" }";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        byte[] imageBytes = restTemplate.postForObject(url, entity, byte[].class);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
