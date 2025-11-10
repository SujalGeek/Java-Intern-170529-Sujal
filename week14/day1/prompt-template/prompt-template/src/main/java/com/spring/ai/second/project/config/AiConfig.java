package com.spring.ai.second.project.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean(name = "ollamaChatClient")
    public ChatClient ollamaChatModel(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
