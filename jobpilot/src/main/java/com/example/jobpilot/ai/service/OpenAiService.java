package com.example.jobpilot.ai.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.resume.dto.ParsedResumeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;


@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

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

    public String getMatchExplanation(String resumeSummary, String jobDescription) {
        String prompt = String.format("""
            You are a job recruiter AI. Analyze the following:

            Resume Summary:
            %s

            Job Description:
            %s

            TASK:
            1. Provide a match score (0–100).
            2. Provide a brief of how well the resume aligns with the job, mentioning key strengths or gaps.
            3. List any missing or weakly represented skills the candidate would need to be a better fit.

            Format your response like this:
            Match Score: <number>
            Explanation: <summary>
            Missing Skills: <comma-separated list>
        """, resumeSummary, jobDescription);

        return getRawResponse(prompt);
    }

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
    public ParsedResumeDTO extractResumeInfo(String resumeText) {
    String prompt = """
    Extract the following details from the resume text below. Return the response as a **single JSON object** with the structure below. 

    The `summary` field must be rich and detailed. It should include:
    - All significant work experiences and roles
    - Key impacts and accomplishments (with quantifiable results where possible)
    - Technologies and tools used
    - Major projects or responsibilities
    - Certifications or standout education achievements

    Use a professional tone and keep the summary informative and ATS-friendly.

    {
    "name": "string",              // Candidate's full name
    "email": "string",             // Email address
    "phone": "string",             // Phone number
    "skills": ["...", "..."],      // Key technical and soft skills
    "atsScore": number,            // A number (0-100) representing how well this resume matches a typical job description
    "summary": "string"            // A detailed, ATS-friendly summary that includes key experience, technologies, impacts, and accomplishments
    }

    Resume Text:
    """ + resumeText;
    
        try {
            String jsonResponse = getRawResponse(prompt); 
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse, ParsedResumeDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse OpenAI response as JSON", e);
        }
    }
    public String improveText(String currentText, String instruction) {
        String prompt = String.format(
            """
            You are a professional job application assistant.
    
            Below is the current cover letter:
    
            ---
            %s
            ---
    
            Improve the above cover letter based on the following instruction:
            "%s"
    
            Provide a rewritten version only. Keep it professional and relevant to job applications.
            """,
            currentText,
            instruction
        );
    
        return getRawResponse(prompt);
    }

    public String generateFollowUpEmailPrompt(Job job) {
        String prompt = String.format("""
            Write a polite and professional follow-up email for the position of %s at %s.

            The applicant has already submitted an application and wants to express continued interest and inquire about the next steps.

            Only return the email body. Do not include any extra commentary, greetings, closing remarks, or instructions. Just the email itself.
        """, job.getTitle(), job.getCompany());

        return getRawResponse(prompt);
    }


    public String buildImprovementPrompt(FollowUpEmail email, String userInstructions) {
        String prompt = String.format("""
            The user has written the following follow-up email:

            %s

            Improve or modify this email based on the following instructions:

            %s

            Only return the improved email body. Do not include any explanations, notes, or extra text—just the revised email itself.
        """, email.getBody(), userInstructions);

        return getRawResponse(prompt);
    }

}
