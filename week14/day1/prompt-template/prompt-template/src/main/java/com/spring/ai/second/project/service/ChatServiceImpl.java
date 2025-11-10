package com.spring.ai.second.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ai.second.project.entity.Tut;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService{

    private ChatClient chatClient;

    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;

    public ChatServiceImpl(ChatClient.Builder builder)
    {
        this.chatClient=builder.build();
    }

    @Override
    public Tut chat(String query) {
        BeanOutputConverter<Tut> converter = new BeanOutputConverter<>(Tut.class);

        String formatInstructions = converter.getFormat(); // Tells model how to format JSON

        String userPrompt = """
        You are a helpful coding assistant.
        Provide the information in the format below:
        {format}

        Question: {query}
        """;

        String renderedPrompt = userPrompt
                .replace("{format}", formatInstructions)
                .replace("{query}", query);

        String response = chatClient
                .prompt()
                .user(u -> u.text(renderedPrompt))
                .call()
                .content();

        System.out.println("Raw model output: " + response);

        return converter.convert(response);
    }

    public String chatTemplate()
    {
        // first step
//        PromptTemplate strTemplate = PromptTemplate.builder().template("What is {techName}? tell me example of {exampleName}").build();
//
//        // second step (render the template)
//        String renderedMessage = strTemplate.render(Map.of(
//                "techName","Spring",
//                "exampleName","Spring Boot"
//        ));
//
//        Prompt prompt = new Prompt(renderedMessage);
//       var content =  this.chatClient.prompt(prompt).call().content();
//        return content;

//        var systemPromptTemplate = SystemPromptTemplate.builder()
//                .template("You are helpful coding assitant. You are expert in coding")
//                .build();
//        var systemMessage = systemPromptTemplate.createMessage();
//        var userPromptTemplate = PromptTemplate.builder().template(
//                "What is {techName}? tell me also about {techExample}"
//        ).build();
//        var userMessage = userPromptTemplate.createMessage(Map.of(
//            "techName","Spring",
//                "techExample","Spring Exception"
//        ));
//        Prompt prompt = new Prompt(systemMessage,userMessage);
//        return this.chatClient.prompt(prompt).call().content();

    // 2nd way chat fluent api
//        return this.chatClient.prompt()
//                .system(system -> system.text("You are helpful coding assistant. You are an expert in coding."))
//                .user(user -> user.text("What is {techName}? tell me also about {techExample}")
//                        .param("techName","Spring")
//                        .param("techExample","Spring Exception"
//                        ))
//                .call()
//                 .content();

        return this.chatClient
                .prompt()
                .system(system -> system.text(this.systemMessage))
                .user(user->user.text(this.userMessage).param("concept","Python iteration"))
                .call()
                .content();
    }


}
