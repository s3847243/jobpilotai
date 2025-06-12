package com.example.jobpilot.job.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResult {
    private Double score;
    private String explanation;
    private List<String> missingSkills;
}