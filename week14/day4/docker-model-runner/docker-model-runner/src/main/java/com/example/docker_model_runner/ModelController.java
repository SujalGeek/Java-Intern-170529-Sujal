package com.example.docker_model_runner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModelController {
    private final ChatClient chatClient;

    public ModelController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/ask")
    public String askModel(@RequestParam(defaultValue = "Tell me a joke about Java") String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}