package com.example.jobpilot.job.dto;

import java.util.List;

import lombok.Data;

@Data
public class JobDetailsDTO {
    private String title;
    private String company;
    private String description;
    private String location;
    private String employmentType;
    private List<String> requiredSkills;
}