package com.example.jobpilot.job.service;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.jobpilot.job.dto.ManualJobRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final OpenAiService openAiService;
    public Job addJobFromUrl(String url, User user) {
        
        Job job = Job.builder()
                .user(user)
                .title("Backend Developer")
                .company("Acme Corp")
                .location("Remote")
                .employmentType("Full-time")
                .description("We are looking for a backend developer skilled in Java, Spring Boot, and AWS.")
                .requiredSkills(List.of("java", "spring boot", "aws"))
                .url(url)
                .source("Manual")
                .status("SAVED")
                .createdAt(Instant.now())
                .build();

        return jobRepository.save(job);
    }

    // === Resume-Job Matching (basic skill overlap) ===
    public String openAiMatchSummary(Job job, Resume resume) {
        return openAiService.getMatchExplanation(
                resume.getParsedSummary(),
                job.getDescription()
        );
    }
    private Double extractScore(String feedback) {
        Matcher matcher = Pattern.compile("(?i)match score[:\\s]+(\\d{1,3})").matcher(feedback);
        if (matcher.find()) {
            int score = Integer.parseInt(matcher.group(1));
            return Math.min(score, 100.0);
        }
        return null;
    }
    
    private List<String> extractMissingSkills(String feedback) {
        Matcher matcher = Pattern.compile("(?i)missing skills[:\\s]+(.+)").matcher(feedback);
        if (matcher.find()) {
            String raw = matcher.group(1);
            return Arrays.stream(raw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return List.of();
    }
    
    public Job matchJobWithResume(Job job, Resume resume) {
        String feedback = openAiMatchSummary(job, resume);
        Double score = extractScore(feedback); 
    
        job.setMatchScore(score);
        job.setMatchFeedback(feedback);
        job.setMissingSkills(extractMissingSkills(feedback));

    
        return jobRepository.save(job);
    }

    public List<Job> getUserJobs(User user) {
        return jobRepository.findByUser(user);
    }

    public Optional<Job> getJobById(UUID jobId) {
        return jobRepository.findById(jobId);
    }

    public Job updateJobStatus(UUID jobId, String newStatus, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        job.setStatus(newStatus);
        return jobRepository.save(job);
    }

    public Job addManualJob(ManualJobRequest request, User user) {
    Job job = Job.builder()
            .user(user)
            .title(request.getTitle())
            .company(request.getCompany())
            .location(request.getLocation())
            .employmentType(request.getEmploymentType())
            .description(request.getDescription())
            .requiredSkills(request.getRequiredSkills())
            .source("Manual")
            .status("SAVED")
            .createdAt(Instant.now())
            .build();

    return jobRepository.save(job);
    }   


    
}