package com.example.jobpilot.job.dto;

import java.util.List;

import lombok.Data;

@Data
public class ManualJobRequest {
    private String title;
    private String company;
    private String location;
    private String employmentType;
    private String description;
    private List<String> requiredSkills;
}