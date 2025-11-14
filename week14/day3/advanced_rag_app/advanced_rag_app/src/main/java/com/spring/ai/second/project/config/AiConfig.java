package com.spring.ai.second.project.config;

import com.spring.ai.second.project.advisors.TokenAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfig {

    private final Logger logger = LoggerFactory.getLogger(AiConfig.class);

    /**
     * Memory disabled by using the minimum allowed (1 message).
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(1)  // required min value
                .build();
    }

    /**
     * ChatClient bean with TokenAdvisor + SafeGuard.
     */
    @Bean(name = "ollamaChatClient")
    public ChatClient ollamaChatModel(OllamaChatModel chatModel, ChatMemory chatMemory) {

        logger.info("ChatMemoryImplementation class: " + chatMemory.getClass().getName());

        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new TokenAdvisor(),
                        new SafeGuardAdvisor(
                                List.of("violence", "hate", "adult") // allow only safe topics
                        )
                )
                .build();
    }
}
