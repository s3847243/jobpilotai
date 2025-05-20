package com.example.jobpilot.resume.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class JobSummaryDTO {
    private UUID id;
    private String title;
    private String company;
    private Double matchScore;
    private String status; // or use an enum if frontend expects it
    private String url;
}