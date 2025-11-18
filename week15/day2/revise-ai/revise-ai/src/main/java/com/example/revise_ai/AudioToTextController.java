package com.example.revise_ai;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioToTextController {

    @Value("${spring.ai.openai.api-key}")    // <- USES YOUR GROQ API KEY
    private String apiKey;

    @Value("${groq.api.url}")
    private String groqUrl;

    @PostMapping("/audio2text")
    public String audioToText(@RequestParam("file") MultipartFile file) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-large-v3");
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> req = new HttpEntity<>(body, headers);

        RestTemplate rest = new RestTemplate();
        return rest.postForObject(groqUrl, req, String.class);
    }
}
