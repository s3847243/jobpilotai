package com.example.jobpilot.job.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final WebClient.Builder webClientBuilder;

    public String getMatchExplanation(String resumeSummary, String jobDescription) {
        String prompt = String.format("""
            You are a job recruiter AI. Analyze the following:
        
            Resume Summary:
            %s
        
            Job Description:
            %s
        
            TASK:
            1. Provide a match score (0–100).
            2. Briefly explain how well the resume matches the job.
            3. List any missing or weakly represented skills the candidate would need to be a better fit.
        
            Format your response like this:
            Match Score: <number>
            Explanation: <summary>
            Missing Skills: <comma-separated list>
        """, resumeSummary, jobDescription);

        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.4
        );

        return webClientBuilder.build()
                .post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
                })
                .block();
    }
}