package com.example.jobpilot.job.mapper;

import org.springframework.stereotype.Component;

import com.example.jobpilot.job.dto.JobSummaryDTO;
import com.example.jobpilot.job.model.Job;
@Component
public class JobSummaryMapper {
    public JobSummaryDTO toSummaryDTO(Job job) {
        return JobSummaryDTO.builder()
            .id(job.getId())
            .title(job.getTitle())
            .company(job.getCompany())
            .matchScore(job.getMatchScore())
            .status(job.getStatus().toString())
            .url(job.getUrl())
            .build();
    }
}
