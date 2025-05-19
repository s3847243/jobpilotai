package com.example.jobpilot.job.mapper;

import org.springframework.stereotype.Component;

import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.model.Job;

@Component
public class JobMapper {

    public JobDTO toDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setDescription(job.getDescription());
        dto.setUrl(job.getUrl());
        dto.setMatchFeedback(job.getMatchFeedback());
        dto.setStatus(job.getStatus()); 
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setMissingSkills(job.getMissingSkills());

        dto.setUserId(job.getUser() != null ? job.getUser().getUserId() : null);
        dto.setResumeId(job.getResume() != null ? job.getResume().getId() : null);
        dto.setCoverLetterId(job.getCoverLetter() != null ? job.getCoverLetter().getId() : null);
        dto.setFollowUpEmailId(job.getFollowUpEmail() != null ? job.getFollowUpEmail().getId() : null);

        return dto;
    }
}