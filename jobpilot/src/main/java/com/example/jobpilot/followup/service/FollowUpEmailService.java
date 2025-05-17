package com.example.jobpilot.followup.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.followup.repository.FollowUpEmailRepository;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowUpEmailService {

    private final FollowUpEmailRepository followUpEmailRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final OpenAiService openAiService;

    // Create and generate a new follow-up email for a job
    public FollowUpEmail generateFollowUpEmail(UUID jobId, Long userId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found or unauthorized"));

        if (job.getFollowUpEmail() != null) {
            throw new RuntimeException("Follow-up email already exists for this job");
        }

        String prompt = buildFollowUpPrompt(job);
        String content = openAiService.getRawResponse(prompt);

        FollowUpEmail email = new FollowUpEmail();
        email.setJob(job);
        email.setSubject("Following up on " + job.getTitle() + " at " + job.getCompany());
        email.setBody(content);
        email.setCreatedAt(Instant.now());

        FollowUpEmail saved = followUpEmailRepository.save(email);
        job.setFollowUpEmail(saved);
        jobRepository.save(job); // optional, since cascade persists follow-up

        return saved;
    }

    // Get follow-up email by ID (with user check)
    public FollowUpEmail getById(Long followUpId, Long userId) {
        return followUpEmailRepository.findByIdAndJobUserId(followUpId, userId)
                .orElseThrow(() -> new RuntimeException("Follow-up not found or unauthorized"));
    }

    // Get all follow-up emails for current user
    public List<FollowUpEmail> getAllForUser(Long userId) {
        return followUpEmailRepository.findAllByJobUserId(userId);
    }

    // Improve/update follow-up email based on user instructions
    public FollowUpEmail improveFollowUpEmail(Long followUpId, Long userId, String instructions) {
        FollowUpEmail email = getById(followUpId, userId);

        String prompt = buildImprovementPrompt(email, instructions);
        String improvedBody = openAiService.getRawResponse(prompt);

        email.setBody(improvedBody);
        return followUpEmailRepository.save(email);
    }

    // 🔧 Prompt builder helpers
    private String buildFollowUpPrompt(Job job) {
        return String.format("""
            Write a polite and professional follow-up email for the position of %s at %s. 
            The applicant has already submitted an application and wants to express continued interest and inquire about the next steps.
        """, job.getTitle(), job.getCompany());
    }

    private String buildImprovementPrompt(FollowUpEmail email, String userInstructions) {
        return String.format("""
            The user has written this follow-up email:\n\n%s\n\n
            Please improve or modify it based on the following instructions:\n%s
        """, email.getBody(), userInstructions);
    }
}
