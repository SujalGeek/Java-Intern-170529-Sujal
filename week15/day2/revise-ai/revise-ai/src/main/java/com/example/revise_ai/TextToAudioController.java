package com.example.revise_ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
@RestController
public class TextToAudioController {

    @Value("${GROQ_API_KEY}")
    private String apiKey;

    @PostMapping("/text2audio")
    public ResponseEntity<byte[]> textToAudio(@RequestParam String text) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = """
        {
            "model": "gpt-4o-mini-tts",
            "voice": "alloy",
            "input": "%s"
        }
        """.formatted(text);

        HttpEntity<String> req = new HttpEntity<>(json, headers);

        RestTemplate rest = new RestTemplate();
        byte[] audioBytes = rest.postForObject(
                "https://api.groq.com/openai/v1/audio/speech",
                req,
                byte[].class
        );

        return ResponseEntity
                .ok()
                .header("Content-Type", "audio/mpeg")
                .header("Content-Disposition", "attachment; filename=speech.mp3")
                .body(audioBytes);
    }
}
