package com.example.jobpilot.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Core method: send prompt and get response
    public String getRawResponse(String prompt) {
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.4
        );

        Map response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }

    // 1️⃣ Used in Resume-Job Matching (match score + feedback + missing skills)
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

        return getRawResponse(prompt);
    }

    // 2️⃣ Used to Extract Job Details (real scraping from pasted job text)
    public String extractJobInfoFromText(String pageText) {
        String prompt = String.format("""
            Extract the following fields as a JSON object:
            - title
            - company
            - description
            - location
            - employmentType
            - requiredSkills (as a list)

            Here is the job listing text:
            %s
        """, pageText);

        return getRawResponse(prompt);
    }

    // 3️⃣ Used to Generate Personalized Cover Letters
    public String generateCoverLetter(String resumeSummary, String jobTitle, String companyName, String jobDescription) {
        String prompt = String.format("""
            You are an expert career coach and recruiter.

            Write a personalized and professional cover letter for the following:

            Resume Summary:
            %s

            Job Title:
            %s

            Company:
            %s

            Job Description:
            %s

            Tone: Formal and enthusiastic
            Length: No more than 300 words
            Format: No header (no name/contact), just the letter body.
        """, resumeSummary, jobTitle, companyName, jobDescription);

        return getRawResponse(prompt);
    }
}
