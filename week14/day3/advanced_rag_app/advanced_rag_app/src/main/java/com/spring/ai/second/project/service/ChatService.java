package com.spring.ai.second.project.service;

import com.spring.ai.second.project.entity.Tut;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
//    Tut chat(String query);
    public String chatTemplate(String query,String userId);

    Flux<String> streamChat(String query);

    String queryResponse(String userQuery);

    void saveData(List<String> list);
}
