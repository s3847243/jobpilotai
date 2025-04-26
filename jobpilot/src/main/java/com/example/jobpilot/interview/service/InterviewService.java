package com.example.jobpilot.interview.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.interview.dto.InterviewQuestionsResponse;
import com.example.jobpilot.interview.dto.InterviewRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final OpenAiService openAiService;
    public InterviewQuestionsResponse generateQuestions(InterviewRequest request) {
        String prompt = String.format("""
            Generate two sets of interview questions for a candidate applying to the role of %s at %s:

            1. Five technical interview questions relevant to the role
            2. Five behavioral interview questions

            Format:
            TECHNICAL:
            - Question 1
            - Question 2
            ...
            BEHAVIORAL:
            - Question 1
            - Question 2
            ...
            """, request.getJobTitle(), request.getCompanyName());

        String response = openAiService.getRawResponse(prompt);

        return parseQuestions(response);
    }

    private InterviewQuestionsResponse parseQuestions(String text) {
        List<String> technical = extractQuestions(text, "TECHNICAL");
        List<String> behavioral = extractQuestions(text, "BEHAVIORAL");

        InterviewQuestionsResponse result = new InterviewQuestionsResponse();
        result.setTechnicalQuestions(technical);
        result.setBehavioralQuestions(behavioral);
        return result;
    }

    private List<String> extractQuestions(String text, String section) {
        Pattern pattern = Pattern.compile(section + ":\\s*((?:-\\s.*\\n?)+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).lines()
                    .map(line -> line.replaceFirst("-\\s*", "").trim())
                    .filter(line -> !line.isBlank())
                    .toList();
        }
        return List.of();
    }
}