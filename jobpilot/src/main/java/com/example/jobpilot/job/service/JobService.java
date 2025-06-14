package com.example.jobpilot.job.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.repository.CoverLetterRepository;
import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.dto.JobDetailsDTO;
import com.example.jobpilot.job.dto.JobSummaryDTO;
import com.example.jobpilot.job.dto.MatchResult;
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
        MatchResult parsedFeedback = parseMatchResult(feedback);
        job.setMatchScore(parsedFeedback.getScore());
        job.setMatchFeedback(parsedFeedback.getExplanation());
        job.setMissingSkills(new ArrayList<>(parsedFeedback.getMissingSkills()));

        return jobMapper.toDTO(jobRepository.save(job));
    }
    private MatchResult parseMatchResult(String feedback) {
        Double score = extractScore(feedback);
        String explanation = extractExplanation(feedback);
        List<String> missingSkills = extractMissingSkills(feedback);

        return MatchResult.builder()
                .score(score)
                .explanation(explanation)
                .missingSkills(missingSkills)
                .build();
    }
    private String extractExplanation(String feedback) {
    Matcher matcher = Pattern.compile("(?i)explanation[:\\s]+(.*?)(?:missing skills:|$)", Pattern.DOTALL).matcher(feedback);
    if (matcher.find()) {
        return matcher.group(1).trim();
    }
    return null;
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
            .orElseThrow(() -> new NoSuchElementException("Job not found")); 

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Unauthorized"); 
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
        // Invalidate old cover letter
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
    public void deleteJobById(UUID jobId, User user) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied: You are not authorized to delete this job.");
        }

        jobRepository.delete(job);
    }
    public List<JobSummaryDTO> findJobsUsingResume(UUID resumeId, User user) {
        return jobRepository.findJobSummariesByResumeIdAndUser(resumeId, user).stream()
            .map(view -> JobSummaryDTO.builder()
                .id(view.getId())
                .title(view.getTitle())
                .company(view.getCompany())
                .matchScore(view.getMatchScore())
                .status(view.getStatus())
                .url(view.getUrl())
                .build()
            ).toList();
    }

    
    
}