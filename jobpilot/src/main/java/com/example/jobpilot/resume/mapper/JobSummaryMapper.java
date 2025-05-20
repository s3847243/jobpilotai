package com.example.jobpilot.resume.mapper;

import org.springframework.stereotype.Component;

import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.resume.dto.JobSummaryDTO;
@Component
public class JobSummaryMapper {
    public JobSummaryDTO toSummaryDTO(Job job) {
        JobSummaryDTO dto = new JobSummaryDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setMatchScore(job.getMatchScore());
        dto.setStatus(job.getStatus().toString());
        dto.setUrl(job.getUrl());
        return dto;
    }
}
