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

import java.io.IOException;
import java.util.Base64;
import java.util.List;
@RestController
public class ImageToTextController {

    @PostMapping("/img2text")
    public String imgToText(@RequestParam("image") MultipartFile image) throws IOException {

        byte[] img = image.getBytes();

        String json = """
        {
            "model": "llava",
            "prompt": "Describe this image in detail.",
            "images": ["%s"]
        }
        """.formatted(Base64.getEncoder().encodeToString(img));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> req = new HttpEntity<>(json, headers);

        RestTemplate rest = new RestTemplate();
        String res = rest.postForObject("http://localhost:11434/api/generate", req, String.class);

        return res;
    }
}
