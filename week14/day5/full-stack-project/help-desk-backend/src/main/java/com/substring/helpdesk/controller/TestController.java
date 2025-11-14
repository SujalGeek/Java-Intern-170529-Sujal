package com.substring.helpdesk.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ChatClient chatClient;

    public TestController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/test-ai")
    public String test() {
        return chatClient.prompt().user("Hello Groq!").call().content();
    }
}
