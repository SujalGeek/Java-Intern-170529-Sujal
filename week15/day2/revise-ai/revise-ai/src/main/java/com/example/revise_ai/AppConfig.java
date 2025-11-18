package com.example.revise_ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Primary
    @Bean
    public ChatModel primaryModel(@Qualifier("openAiChatModel") ChatModel chatModel)
    {
        return chatModel;
    }
}
