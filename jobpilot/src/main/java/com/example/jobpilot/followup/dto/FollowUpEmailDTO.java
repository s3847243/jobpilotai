package com.example.jobpilot.followup.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpEmailDTO {

    private UUID id;
    private String subject;
    private String body;
    private Instant createdAt;
    private UUID jobId; // Reference only, not the full Job

    // Getters & Setters
}
