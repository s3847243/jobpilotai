package com.example.jobpilot.resume.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private UUID id;
    private String filename;
    private String s3Url;
    private String parsedName;
    private String parsedEmail;
    private String parsedPhone;
    private List<String> parsedSkills;
    private String parsedSummary;
    private Instant uploadedAt;
    private Double atsScore;
    private UUID userId; 
}