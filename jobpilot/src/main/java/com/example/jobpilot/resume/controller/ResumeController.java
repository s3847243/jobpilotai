package com.example.jobpilot.resume.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.job.dto.JobSummaryDTO;
import com.example.jobpilot.resume.dto.ResumeDTO;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.UserPrincipal;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeDTO> uploadResume(
            @RequestParam("file") MultipartFile file,
             @AuthenticationPrincipal UserPrincipal userPrincipal
    ) throws IOException {
        
        ResumeDTO resumeDTO = resumeService.uploadResume(file, userPrincipal.getUser());
        return ResponseEntity.ok(resumeDTO);
    }


    @DeleteMapping("/{resumeId}")
    public ResponseEntity<String> deleteResume(@PathVariable UUID resumeId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        resumeService.deleteResume(resumeId, userPrincipal.getUser());
        return ResponseEntity.ok("Resume deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<ResumeDTO>> getAllResumes(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        System.out.println("User = " + userPrincipal.getUser()); // Check if it's null

        List<ResumeDTO> resumes = resumeService.getResumesByUser(userPrincipal.getUser());
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDTO> getResume(
            @PathVariable UUID resumeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        ResumeDTO resume = resumeService.getResumeByIdForUser(resumeId, userPrincipal.getUser());
        return ResponseEntity.ok(resume);
    }

    // @GetMapping("/{resumeId}/jobs")
    // public ResponseEntity<List<JobSummaryDTO>> getJobsForResume(
    //         @PathVariable UUID resumeId,
    //         @AuthenticationPrincipal UserPrincipal userPrincipal
    // ) {
    //     List<JobSummaryDTO> jobs = resumeService.getJobsForResume(resumeId, userPrincipal.getUser());
    //     return ResponseEntity.ok(jobs);
    // }



}
