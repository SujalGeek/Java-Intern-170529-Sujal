package com.spring.ai.second.project.controller;

import com.spring.ai.second.project.entity.Tut;
import com.spring.ai.second.project.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ChatController {

//    private ChatClient ollamaChatClient;

    private ChatService chatService;


//    public ChatController(@Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
////        this.openAiChatClient = openAiChatClient;
//        this.ollamaChatClient = ollamaChatClient;
//    }



    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ResponseEntity<Tut> chat(@RequestParam(value = "q") String query)
    {
//        var ResultResponse = this.ollamaChatClient.prompt(query).call().content();
//return ResponseEntity.ok(ResultResponse);
        return ResponseEntity.ok(chatService.chat(query));
    }

}
