package com.example.revise_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.memory.;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class GroqAIController {

//    private final ChatClient chatClient;

//    private final EmbeddingModel embeddingModel;




    private ChatClient chatClient;
//    public GroqAIController (@Qualifier("openAiChatModel") ChatModel chatModel)
//    {
//        this.chatClient = ChatClient.builder(chatModel).build();
//    }

    // Basicalyy added the App Config to use the ChatClient Builder directly
    // configure it in the code base

    // chat memory
//    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

/*    public GroqAIController(ChatClient.Builder builder)
    {
//        this.chatClient=builder
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    } */


    // Basically for storing the previous conversation we can use the
    // advisor like ChatMemoryAdvisor so that it remembers the previous
    // conversation and it continues to answer as per that conversation

    public GroqAIController(ChatModel chatModel)
    {
    this.chatClient = ChatClient.create(chatModel);
    }



    @GetMapping("/ask/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message) {

        // This is the way to use the chatClient
//        String response = chatClient
//                .prompt(message)
//                .call()
//                .content();
//
//        return ResponseEntity.ok(response);


        // This is the way you should use the chat Response
        ChatResponse chatResponse = chatClient.prompt(message)
                .call().chatResponse();

        System.out.println(chatResponse.getMetadata().getModel());

        String response = chatResponse
                .getResult()
                .getOutput()
                .getText();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/recommend")
    public String recommend(@RequestParam String type,@RequestParam String year,@RequestParam String lang)
    {

        String temp = """
            I want to watch a {type} movie tonight with good rating.
            Looking for movies released around {year}.
            The language should be {lang}.
            
            Suggest ONE SPECIFIC movie and follow EXACTLY this format:
            
            1. Movie name:
            2. Basic Plot:
            3. Cast:
            4. Length:
            5. IMDB rating:
            """;

        System.out.println(temp);

        PromptTemplate template = new PromptTemplate(temp);

        Prompt prompt = template.create(
                Map.of(
                        "type", type,
                        "year", year,
                        "lang", lang
                )
        );

        String response = chatClient.prompt(prompt)
                .call()
                .content();
        System.out.println(response);
        return  response;
    }



}
