package com.example.jobpilot.followup.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.mappers.FollowUpEmailMapper;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.followup.repository.FollowUpEmailRepository;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowUpEmailService {

    private final FollowUpEmailRepository followUpEmailRepository;
    private final JobRepository jobRepository;
    private final OpenAiService openAiService;
    private final FollowUpEmailMapper followUpEmailMapper;

    public FollowUpEmailDTO generateFollowUpEmail(UUID jobId, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found or unauthorized"));

        if (job.getFollowUpEmail() != null) {
            throw new RuntimeException("Follow-up email already exists for this job");
        }

        String content = openAiService.generateFollowUpEmailPrompt(job);


        FollowUpEmail email = new FollowUpEmail();
        email.setJob(job);
        email.setUser(user);
        email.setSubject("Following up on " + job.getTitle() + " at " + job.getCompany());
        email.setBody(content);
        email.setCreatedAt(Instant.now());
        email.setFollowUpEmailName("Email at "+ job.getCompany());

        FollowUpEmail saved = followUpEmailRepository.save(email);
        job.setFollowUpEmail(saved);
        jobRepository.save(job); 

        return followUpEmailMapper.toDTO(saved);
    }

    public FollowUpEmailDTO getById(UUID followUpId, UUID userId) {
        FollowUpEmail email = followUpEmailRepository.findByIdAndUserId(followUpId, userId)
                .orElseThrow(() -> new RuntimeException("Follow-up not found or unauthorized"));
        return followUpEmailMapper.toDTO(email);
    }

    public List<FollowUpEmailDTO> getAllForUser(UUID userId) {
        return followUpEmailRepository.findAllByUserId(userId).stream()
                .map(followUpEmailMapper::toDTO)
                .toList();
    }

    public FollowUpEmailDTO improveFollowUpEmail(UUID followUpId, UUID userId, String instructions) {
        FollowUpEmail email = followUpEmailRepository.findByIdAndUserId(followUpId, userId)
                .orElseThrow(() -> new RuntimeException("Follow-up not found or unauthorized"));

        if (instructions == null || instructions.trim().isEmpty()) {
            throw new IllegalArgumentException("Improvement instructions cannot be empty");
        }

        String improvedBody = openAiService.buildImprovementPrompt(email, instructions);

        email.setBody(improvedBody);
        FollowUpEmail updated = followUpEmailRepository.save(email);

        return followUpEmailMapper.toDTO(updated);
    }

   

    public void deleteFollowUpEmail(UUID followUpEmailId, User user) {
        FollowUpEmail followUpEmail = followUpEmailRepository.findById(followUpEmailId)
            .orElseThrow(() -> new RuntimeException("Follow-up email not found"));

        if (!followUpEmail.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You are not authorized to delete this follow-up email.");
        }

        followUpEmailRepository.delete(followUpEmail);
    }
    
}
