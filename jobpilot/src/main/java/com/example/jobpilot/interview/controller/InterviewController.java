package com.example.jobpilot.interview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.interview.dto.InterviewQuestionsResponse;
import com.example.jobpilot.interview.dto.InterviewRequest;
import com.example.jobpilot.interview.service.InterviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/questions")
    public ResponseEntity<InterviewQuestionsResponse> generateQuestions(@RequestBody InterviewRequest request) {
        return ResponseEntity.ok(interviewService.generateQuestions(request));
    }
}