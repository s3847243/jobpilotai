package com.example.jobpilot.resume.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobSummaryDTO {
    private UUID id;
    private String title;
    private String company;
    private Double matchScore;
    private String status; 
    private String url;
}