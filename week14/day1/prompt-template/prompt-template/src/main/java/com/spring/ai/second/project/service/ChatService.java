package com.spring.ai.second.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.ai.second.project.entity.Tut;

public interface ChatService {
    Tut chat(String query);
    public String chatTemplate();
}
