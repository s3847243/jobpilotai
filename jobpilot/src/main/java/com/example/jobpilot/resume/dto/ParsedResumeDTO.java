package com.example.jobpilot.resume.dto;

import java.util.List;

import lombok.Data;

@Data
public class ParsedResumeDTO {
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private String summary;
}
