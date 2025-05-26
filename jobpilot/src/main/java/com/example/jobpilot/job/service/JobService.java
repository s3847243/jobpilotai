package com.example.jobpilot.job.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.repository.CoverLetterRepository;
import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.dto.JobDetailsDTO;
import com.example.jobpilot.job.dto.ManualJobRequest;
import com.example.jobpilot.job.mapper.JobMapper;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.model.JobStatus;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final OpenAiService openAiService;
    private final ResumeService resumeService;
    private final ResumeRepository resumeRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final JobMapper jobMapper;
    public JobDTO  addJobFromUrl(String url, User user, UUID resumeId) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10_000)
                    .get();

            String pageText = doc.body().text();

            String aiJson = openAiService.extractJobInfoFromText(pageText);

            ObjectMapper mapper = new ObjectMapper();
            JobDetailsDTO parsed = mapper.readValue(aiJson, JobDetailsDTO.class);
            Resume resume = null;
            if (resumeId != null) {
                resume = resumeService.getResumeEntityByIdForUser(resumeId);
            }
            Job job = Job.builder()
                    .user(user)
                    .resume(resume)
                    .title(parsed.getTitle())
                    .company(parsed.getCompany())
                    .location(parsed.getLocation())
                    .employmentType(parsed.getEmploymentType())
                    .description(parsed.getDescription())
                    .url(url)
                    .source("OpenAI")
                    .status(JobStatus.SAVED) 
                    .createdAt(Instant.now())
                    .build();

            return jobMapper.toDTO(jobRepository.save(job));

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
    
    public JobDTO  matchJobWithResume(Job job, Resume resume) {
        String feedback = openAiMatchSummary(job, resume);
        Double score = extractScore(feedback); 
    
        job.setMatchScore(score);
        job.setMatchFeedback(feedback);
        job.setMissingSkills(new ArrayList<>(extractMissingSkills(feedback)));

    
        return jobMapper.toDTO(jobRepository.save(job));
    }
    public Job getJobEntityById(UUID jobId, User user) {
    Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

    if (!job.getUser().getUserId().equals(user.getUserId())) {
        throw new RuntimeException("Unauthorized");
    }

    return job;
    }
    public List<JobDTO> getUserJobs(User user) {
        return jobRepository.findByUser(user).stream()
                .map(jobMapper::toDTO)
                .toList();
    }
    public JobDTO getJobById(UUID id, User user) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        return jobMapper.toDTO(job);
    }

    public JobDTO  updateJobStatus(UUID jobId, String status, User user) {
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
    
        return jobMapper.toDTO(jobRepository.save(job));
    }

    public JobDTO  addManualJob(ManualJobRequest request, User user) {
    Job job = Job.builder()
            .user(user)
            .title(request.getTitle())
            .company(request.getCompany())
            .location(request.getLocation())
            .employmentType(request.getEmploymentType())
            .description(request.getDescription())
            .source("Manual")
            .status(JobStatus.SAVED) 
            .createdAt(Instant.now())
            .build();

    return jobMapper.toDTO(jobRepository.save(job));
    }   


    public JobDTO  replaceResume(UUID jobId, Resume newResume, User user) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }
        // Delete the old resume file and DB entry
        Resume oldResume = job.getResume();
        resumeService.deleteResume(oldResume.getId(),user);
        job.setResume(newResume);
            // Invalidate old cover letter
        job.setCoverLetter(null);
        job.setMatchScore(null);
        job.setMatchFeedback(null);
        job.setMissingSkills(null);
        return jobMapper.toDTO(jobRepository.save(job));
    }
    @Transactional
    public JobDTO  assignResume(UUID jobId, UUID resumeId, User user) {
        System.out.println("it is getting here");

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume  not found"));
        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        job.setResume(resume);
        
        job.setMatchScore(null);
        job.setMatchFeedback(null);
        job.setMissingSkills(null);
        // 🔥 Invalidate old cover letter
        CoverLetter existingCoverLetter = job.getCoverLetter();
        if (existingCoverLetter != null) {
            String coverLetterText = openAiService.generateCoverLetter(
                resume.getParsedSummary(),
                job.getTitle(),
                job.getCompany(),
                job.getDescription()
                );
            existingCoverLetter.setContent(coverLetterText);
            existingCoverLetter.setUpdatedAt(Instant.now());
            existingCoverLetter.setFinalVersion(false);
            coverLetterRepository.save(existingCoverLetter);
        }

        return jobMapper.toDTO(jobRepository.save(job));

    }
    
}