package com.spring.ai.second.project.service;

import com.spring.ai.second.project.entity.Tut;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService{

    private ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder builder)
    {
        this.chatClient=builder.build();
    }

    @Override
    public Tut chat(String query) {

//       String prompt = "tell me about the Virat Kohli";
//
       // call the llm for response

        Prompt prompt1 = new Prompt(query);

//        String content = chatClient.prompt(prompt)
//                .user(prompt)
//                .system("As an expert in cricket.")
//                .call()
//                .content();

//        var content = chatClient.prompt(prompt1)
//
        System.out.println("The prompt is: "+prompt1);
            Tut tutorial = chatClient.prompt(prompt1)
//                .system("As an expert in cricket.")
                .call()
                .entity(Tut.class);

//                .chatResponse()
//                .getResult()
//                .getOutput()
//                .getText();


//        System.out.println("There is some content "+content);
//
//        return content;

        System.out.println("The tutorial is: "+tutorial);
        return tutorial;
    }
}
