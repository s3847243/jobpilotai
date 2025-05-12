package com.example.jobpilot.job.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.job.dto.JobDetailsDTO;
import com.example.jobpilot.job.dto.ManualJobRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.model.JobStatus;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final OpenAiService openAiService;
    private final ResumeService resumeService;
    public Job addJobFromUrl(String url, User user, Resume resume) {
        try {
            // Step 1: Fetch job page
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10_000)
                    .get();

            String pageText = doc.body().text();

            // Step 2: Ask OpenAI to extract job info
            String aiJson = openAiService.extractJobInfoFromText(pageText);

            // Step 3: Convert JSON to Java object
            ObjectMapper mapper = new ObjectMapper();
            JobDetailsDTO parsed = mapper.readValue(aiJson, JobDetailsDTO.class);

            // Step 4: Save to DB
            Job job = Job.builder()
                    .user(user)
                    .resume(resume)
                    .title(parsed.getTitle())
                    .company(parsed.getCompany())
                    .location(parsed.getLocation())
                    .employmentType(parsed.getEmploymentType())
                    .description(parsed.getDescription())
                    .requiredSkills(parsed.getRequiredSkills())
                    .url(url)
                    .source("OpenAI")
                    .status(JobStatus.SAVED) 
                    .createdAt(Instant.now())
                    .build();

            return jobRepository.save(job);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract job from URL", e);
        }
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
        job.setMissingSkills(new ArrayList<>(extractMissingSkills(feedback)));

    
        return jobRepository.save(job);
    }

    public List<Job> getUserJobs(User user) {
        return jobRepository.findByUser(user);
    }

    public Optional<Job> getJobById(UUID jobId) {
        return jobRepository.findById(jobId);
    }

    public Job updateJobStatus(UUID jobId, String status, User user) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    
        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to update this job");
        }
    
        try {
            JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
            job.setStatus(jobStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    
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
            .status(JobStatus.SAVED) 
            .createdAt(Instant.now())
            .build();

    return jobRepository.save(job);
    }   
    public Job generateAndStoreCoverLetter(UUID jobId, User user) {
        Job job = getJobById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    
        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
    
        Resume resume = job.getResume();
        if (resume == null) {
            throw new RuntimeException("Resume not found for job");
        }
    
        String coverLetter = openAiService.generateCoverLetter(
                resume.getParsedSummary(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription()
        );
    
        job.setCoverLetter(coverLetter);
        return jobRepository.save(job);
    }
    public Job updateCoverLetter(UUID jobId, String newCoverLetter) {
        Job job = getJobById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));;
        job.setCoverLetter(newCoverLetter);
        return jobRepository.save(job);
    }

    public String improveCoverLetter(UUID jobId, String userInstruction) {
        Job job = getJobById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));;
        String currentLetter = job.getCoverLetter();
        if (currentLetter == null || currentLetter.isBlank()) {
            throw new RuntimeException("Cover letter is empty");
        }

        // Call OpenAI service to improve
        return openAiService.improveText(currentLetter, userInstruction);
    }
    

    public Job replaceResume(UUID jobId, Resume newResume, User user) {
        Job job = getJobById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        // Delete the old resume file and DB entry
        Resume oldResume = job.getResume();
        resumeService.deleteFile(oldResume);
        job.setResume(newResume);
            // Invalidate old cover letter
        job.setCoverLetter(null);
        job.setMatchScore(null);
        job.setMatchFeedback(null);
        job.setMissingSkills(null);
        return jobRepository.save(job);
    }
    
}