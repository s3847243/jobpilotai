package com.example.jobpilot.job.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.auth.repository.UserRepository;
import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.job.dto.ManualJobRequest;
import com.example.jobpilot.job.dto.UpdateJobStatusRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JwtService jwtService;

    private User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/from-url")
    public ResponseEntity<Job> addFromUrl(@RequestParam String url, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        Job job = jobService.addJobFromUrl(url, user);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{jobId}/match")
    public ResponseEntity<Job> matchJob(@PathVariable UUID jobId, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        Resume latestResume = resumeRepository.findByUser(user).stream()
                .sorted((a, b) -> b.getUploadedAt().compareTo(a.getUploadedAt()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No resumes uploaded"));

        Job job = jobService.getJobById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Job updated = jobService.matchJobWithResume(job, latestResume);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Job>> listJobs(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseEntity.ok(jobService.getUserJobs(user));
    }

    @PatchMapping("/{jobId}/status")
    public ResponseEntity<Job> updateStatus(
            @PathVariable UUID jobId,
            @RequestBody UpdateJobStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        User user = getUserFromRequest(httpRequest);
        Job updatedJob = jobService.updateJobStatus(jobId, request.getStatus(), user);
        return ResponseEntity.ok(updatedJob);
    }
    @PostMapping("/manual")
    public ResponseEntity<Job> addManualJob(@RequestBody ManualJobRequest request, HttpServletRequest httpRequest) {
        User user = getUserFromRequest(httpRequest);
        Job job = jobService.addManualJob(request, user);
        return ResponseEntity.ok(job);
    }

}