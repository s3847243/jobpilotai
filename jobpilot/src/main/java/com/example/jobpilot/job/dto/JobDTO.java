package com.example.jobpilot.job.dto;

import java.util.List;
import java.util.UUID;

import com.example.jobpilot.job.model.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private UUID id;

    private String title;
    private String company;
    private String description;
    private String url;
    private JobStatus status;
    private String matchFeedback;
    private List<String> missingSkills;
    private Double matchScore; // Calculated value

    private UUID userId;            // Not exposing full User entity
    private UUID resumeId;          // Just linking resume
    private UUID coverLetterId;     // Optional
    private UUID followUpEmailId;   // Optional
}
