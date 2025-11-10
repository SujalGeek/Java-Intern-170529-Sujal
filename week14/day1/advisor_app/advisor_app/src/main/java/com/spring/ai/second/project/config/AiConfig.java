package com.spring.ai.second.project.config;

import com.spring.ai.second.project.advisors.TokenAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfig {

    private Logger logger = LoggerFactory.getLogger(AiConfig.class);

    @Bean(name = "ollamaChatClient")
    public ChatClient ollamaChatModel(OllamaChatModel chatModel, ChatMemory chatMemory) {

        this.logger.info("ChatMemoryImplementation class: "+chatMemory.getClass().getName());
        MessageChatMemoryAdvisor messageChatMemoryAdvisor=  MessageChatMemoryAdvisor.builder(chatMemory).build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(new TokenAdvisor(),new SafeGuardAdvisor(List.of("games")),messageChatMemoryAdvisor)
                .build();
    }
}
