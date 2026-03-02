package com.example.assignment_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class SubmitAssignmentRequest {

    private Long assignmentId;
//    private Long studentId;
    private List<StudentAnswerDto> answers;
}