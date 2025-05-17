package com.example.jobpilot.followup.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.dto.ImproveEmailRequest;
import com.example.jobpilot.followup.mappers.FollowUpEmailMapper;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.followup.service.FollowUpEmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follow-up")
@RequiredArgsConstructor
public class FollowUpEmailController {

    private final FollowUpEmailService followUpEmailService;
    private final FollowUpEmailMapper followUpEmailMapper;

    // 📌 Generate a follow-up email for a job
    @PostMapping("/generate/{jobId}")
    public ResponseEntity<FollowUpEmailDTO> generateFollowUpEmail(@PathVariable UUID jobId) {
        FollowUpEmail email = followUpEmailService.generateFollowUpEmail(jobId, null); // userId can be null for now
        return ResponseEntity.ok(followUpEmailMapper.toDTO(email));
    }

    // 📌 Get a follow-up email by ID
    @GetMapping("/{followUpId}")
    public ResponseEntity<FollowUpEmailDTO> getFollowUpById(@PathVariable Long followUpId) {
        FollowUpEmail email = followUpEmailService.getById(followUpId, null);
        return ResponseEntity.ok(followUpEmailMapper.toDTO(email));
    }

    // 📌 Get all follow-up emails for the current user
    @GetMapping("/all")
    public ResponseEntity<List<FollowUpEmailDTO>> getAllForUser() {
        List<FollowUpEmail> emails = followUpEmailService.getAllForUser(null);
        return ResponseEntity.ok(emails.stream()
                .map(followUpEmailMapper::toDTO)
                .toList());
    }

    // 📌 Improve an existing follow-up email
    @PutMapping("/{followUpId}/improve")
    public ResponseEntity<FollowUpEmailDTO> improveFollowUpEmail(
            @PathVariable Long followUpId,
            @RequestBody ImproveEmailRequest request
    ) {
        FollowUpEmail improved = followUpEmailService.improveFollowUpEmail(followUpId, null, request.getInstructions());
        return ResponseEntity.ok(followUpEmailMapper.toDTO(improved));
    }
}