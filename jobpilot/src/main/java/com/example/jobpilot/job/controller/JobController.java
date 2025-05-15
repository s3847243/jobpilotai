package com.example.jobpilot.job.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.user.repository.UserRepository;
import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.job.dto.CoverLetterResponse;
import com.example.jobpilot.job.dto.ManualJobRequest;
import com.example.jobpilot.job.dto.UpdateJobStatusRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ResumeService resumeService;


    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new RuntimeException("JWT token not found in cookies");
    }
    
    private User getUserFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/from-url")
    public ResponseEntity<Job> addFromUrl(
        @RequestParam String url,
        @RequestParam(required = false) UUID resumeId,
        HttpServletRequest request
    ) throws IOException {
        User user = getUserFromRequest(request);
        Resume resume = null;

        if (resumeId != null) {
            resume = resumeService.getResumeByIdForUser(resumeId, user);
        }

        Job job = jobService.addJobFromUrl(url, user, resume);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{jobId}/match")
    public ResponseEntity<Job> matchJob(@PathVariable UUID jobId, HttpServletRequest request) {
        User user = getUserFromRequest(request);
    
        Job job = jobService.getJobById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    
        if (!job.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to match this job");
        }
    
        Resume resume = job.getResume();
        if (resume == null) {
            throw new RuntimeException("This job has no resume assigned");
        }
    
        Job updated = jobService.matchJobWithResume(job, resume);
        return ResponseEntity.ok(updated);
    }
    

    @GetMapping
    public ResponseEntity<List<Job>> listJobs(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseEntity.ok(jobService.getUserJobs(user));
    }
    @GetMapping("/{jobId}")
    public ResponseEntity< Optional<Job>> getJobById(
            @PathVariable UUID jobId,
            HttpServletRequest httpRequest
    ) {
        User user = getUserFromRequest(httpRequest);
        Optional<Job> job = jobService.getJobById(jobId);
        return ResponseEntity.ok(job);
    }
    @PatchMapping("/{jobId}/status")
    public ResponseEntity<Job> updateStatus(
            @PathVariable UUID jobId,
            @RequestBody UpdateJobStatusRequest request,
            HttpServletRequest httpRequest
    ) {
        User user = getUserFromRequest(httpRequest);
        Job updatedJob = jobService.updateJobStatus(jobId, request.getStatus().toString(), user);
        return ResponseEntity.ok(updatedJob);
    }
    @PostMapping("/manual")
    public ResponseEntity<Job> addManualJob(@RequestBody ManualJobRequest request, HttpServletRequest httpRequest) {
        User user = getUserFromRequest(httpRequest);
        Job job = jobService.addManualJob(request, user);
        return ResponseEntity.ok(job);
    }
    // @CrossOrigin(
    // origins = "http://localhost:5173",
    // allowCredentials = "true"
    // )
    // @PostMapping("/{jobId}/generate-cover-letter")
    // public ResponseEntity<CoverLetterResponse> generateCoverLetter(
    //         @PathVariable UUID jobId,
    //         HttpServletRequest request
    // ) 
    // {
    //     User user = getUserFromRequest(request);
    //     Job updatedJob = jobService.generateAndStoreCoverLetter(jobId, user);
    //     return ResponseEntity.ok(new CoverLetterResponse(updatedJob.getCoverLetter()));
    // }


    // @GetMapping("/{jobId}/cover-letter")
    // public ResponseEntity<CoverLetterResponse> getCoverLetter(@PathVariable UUID jobId, HttpServletRequest request) {
    //     User user = getUserFromRequest(request);
    //     Job job = jobService.getJobById(jobId)
    //             .orElseThrow(() -> new RuntimeException("Job not found"));

    //     if (!job.getUser().getUserId().equals(user.getUserId())) {
    //         throw new RuntimeException("Unauthorized");
    //     }

    //     return ResponseEntity.ok(new CoverLetterResponse(job.getCoverLetter()));
    // }
    // @PutMapping("/{jobId}/cover-letter")
    // public ResponseEntity<Job> updateCoverLetter(
    //         @PathVariable UUID jobId,
    //         @RequestBody String newCoverLetter
    // ) {
    //     Job updated = jobService.updateCoverLetter(jobId, newCoverLetter);
    //     return ResponseEntity.ok(updated);
    // }
    // @PostMapping("/{jobId}/improve-cover-letter")
    // public ResponseEntity<String> improveCoverLetter(
    //         @PathVariable UUID jobId,
    //         @RequestBody Map<String, String> payload
    // ) {
    //     String instruction = payload.get("instruction");
    //     if (instruction == null || instruction.isBlank()) {
    //         return ResponseEntity.badRequest().body("Instruction is required");
    //     }

    //     String improvedText = jobService.improveCoverLetter(jobId, instruction);
    //     return ResponseEntity.ok(improvedText);
    // }

    @PutMapping("/{jobId}/resume")
    public ResponseEntity<Job> replaceResumeForJob(
        @PathVariable UUID jobId,
        @RequestParam("file") MultipartFile file,
        HttpServletRequest request
    ) throws IOException {
        User user = getUserFromRequest(request);
        Resume newResume = resumeService.uploadResume(file, user);
        Job updatedJob = jobService.replaceResume(jobId, newResume, user);
        return ResponseEntity.ok(updatedJob);
    }
    @PutMapping("/job/{jobId}/resume")
    public ResponseEntity<Job> assignResumeToJob(
            @PathVariable UUID jobId,
            @RequestParam UUID resumeId,
            HttpServletRequest request
    ) {
        User user = getUserFromRequest(request);
        Resume resume = resumeService.getResumeByIdForUser(resumeId, user);
        Job job = jobService.assignResume(jobId, resume, user);
        return ResponseEntity.ok(job);
    }
    // @GetMapping("/cover-letters")
    // public ResponseEntity<List<Job>> getAllJobsWithCoverLetters(HttpServletRequest request) {
    //     User user = getUserFromRequest(request);
    //     // List<Job> jobsWithCL = jobRepository.findByUserAndCoverLetterIsNotNull(user);
    //     // return ResponseEntity.ok(jobsWithCL);
    // }
}