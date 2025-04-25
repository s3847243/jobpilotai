package com.example.jobpilot.interview.dto;

import java.util.List;

import lombok.Data;

@Data
public class InterviewQuestionsResponse {
    private List<String> technicalQuestions;
    private List<String> behavioralQuestions;
}