package com.spring.ai.second.project.controller;

import com.spring.ai.second.project.entity.Tut;
import com.spring.ai.second.project.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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

//    @GetMapping("/chat")
//    public ResponseEntity<String> chat(@RequestParam(value = "q") String query,
//                                       @RequestHeader("userId") String userId)
//    {
////        var ResultResponse = this.ollamaChatClient.prompt(query).call().content();
////return ResponseEntity.ok(ResultResponse);
//        return ResponseEntity.ok(chatService.chatTemplate(query,userId));
//    }

    @GetMapping("/chat")
    public ResponseEntity<String> getResponse(@RequestParam("q") String userQuery)
    {
        return ResponseEntity.ok(chatService.queryResponse(userQuery));
    }

    @GetMapping("/stream-chat")
    public ResponseEntity<Flux<String>> streamChat(
            @RequestParam("q") String query
    ){
        return ResponseEntity.ok(this.chatService.streamChat(query));
    }
}
