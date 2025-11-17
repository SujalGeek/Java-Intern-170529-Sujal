package com.example.revise_ai;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.ai.

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController
@RequestMapping("/image")
public class ImageToTextController {

    @Value("${hf.api.key}")
    private String hfApiKey;

    @PostMapping("/describe")
    public String describeImage(@RequestParam("file") MultipartFile file) throws Exception {

        String url = "https://router.huggingface.co/hf-inference/models/llava-hf/llava-1.5-7b-hf";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hfApiKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        return restTemplate.postForObject(url, requestEntity, String.class);
    }
}
