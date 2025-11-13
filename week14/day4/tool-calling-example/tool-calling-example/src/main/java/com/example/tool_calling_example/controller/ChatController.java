package com.example.tool_calling_example.controller;

import com.example.tool_calling_example.services.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ask")
public class ChatController {

    private ChatService chatService;

    public ChatController (ChatService chatService)
    {
        this.chatService=chatService;
    }




    @GetMapping
    public String chat(@RequestParam(required = true) String q){
        return chatService.chat(q);
    }
}
