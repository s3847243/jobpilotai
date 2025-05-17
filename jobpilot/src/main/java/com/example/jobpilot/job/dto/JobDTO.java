package com.example.jobpilot.job.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobDTO {
    private UUID id;
    private String jobTitle;
    private String company;
    private UUID followUpEmailId; // Optional
}
