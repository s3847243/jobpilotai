package com.example.jobpilot.coverletter.service;

import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoverLetterService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final OpenAiService openAiService;

    public CoverLetterResponse generateCoverLetter(CoverLetterRequest request) {
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        String prompt = """
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
            """.formatted(
                resume.getParsedSummary() != null ? resume.getParsedSummary() : "No summary available.",
                job.getTitle(),
                job.getCompany(),
                job.getDescription()
            );

        String coverLetterText = openAiService.getRawResponse(prompt);

        CoverLetterResponse response = new CoverLetterResponse();
        response.setCoverLetterText(coverLetterText);

        return response;
    }
}
