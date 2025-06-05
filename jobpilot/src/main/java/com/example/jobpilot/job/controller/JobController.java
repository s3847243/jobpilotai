package com.example.jobpilot.job.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.dto.UpdateJobStatusRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ResumeService resumeService;

    @PostMapping("/from-url")
    public ResponseEntity<JobDTO> addFromUrl(
        @RequestParam String url,
        @RequestParam(required = false) UUID resumeId,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws IOException {
        JobDTO job = jobService.addJobFromUrl(url, userPrincipal.getUser(), resumeId);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{jobId}/match")
    public ResponseEntity<JobDTO> matchJob(@PathVariable UUID jobId, @AuthenticationPrincipal UserPrincipal userPrincipal) {


        Job job = jobService.getJobEntityById(jobId, userPrincipal.getUser()); // New method returning Job
        Resume resume = job.getResume();

        if (resume == null) {
            throw new RuntimeException("This job has no resume assigned");
        }

        JobDTO updated = jobService.matchJobWithResume(job, resume);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> listJobs(@AuthenticationPrincipal UserPrincipal userPrincipal) {
   
        List<JobDTO> jobDTOs = jobService.getUserJobs(userPrincipal.getUser()).stream()
                .toList();
        return ResponseEntity.ok(jobDTOs);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDTO> getJobById(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        
        JobDTO job = jobService.getJobById(jobId,userPrincipal.getUser());

        return ResponseEntity.ok(job);
    }

    @PatchMapping("/{jobId}/status")
    public ResponseEntity<JobDTO> updateStatus(
            @PathVariable UUID jobId,
            @RequestBody UpdateJobStatusRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        JobDTO updatedJob = jobService.updateJobStatus(jobId, request.getStatus().toString(), userPrincipal.getUser());
        return ResponseEntity.ok(updatedJob);
    }

    
    @PutMapping("/{jobId}/resume")
    public ResponseEntity<JobDTO> replaceResumeForJob(
        @PathVariable UUID jobId,
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws IOException {
        Resume newResume = resumeService.internalUploadResume(file, userPrincipal.getUser());
        JobDTO updatedJob = jobService.replaceResume(jobId, newResume, userPrincipal.getUser());
        return ResponseEntity.ok(updatedJob);
    }

    @PutMapping("/{jobId}/assign-resume/{resumeId}")
    public ResponseEntity<JobDTO> assignResumeToJob(
            @PathVariable UUID jobId,
            @PathVariable UUID resumeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // Resume resume = resumeService.getResumeByIdForUser(resumeId, userPrincipal.getUser());
        JobDTO updated = jobService.assignResume(jobId, resumeId, userPrincipal.getUser());
            
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteJobById(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        jobService.deleteJobById(jobId, userPrincipal.getUser());
        return ResponseEntity.ok("Job deleted successfully.");
    }   
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

}