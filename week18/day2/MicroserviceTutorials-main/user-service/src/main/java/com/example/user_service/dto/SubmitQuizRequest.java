package com.example.user_service.dto;

import java.util.Map;
import lombok.Data;

@Data
public class SubmitQuizRequest {
    private Integer quizId;
    private Map<Long, String> answers;
}
