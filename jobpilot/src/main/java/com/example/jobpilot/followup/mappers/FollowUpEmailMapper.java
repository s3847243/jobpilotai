package com.example.jobpilot.followup.mappers;

import org.springframework.stereotype.Component;

import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.job.model.Job;

@Component
public class FollowUpEmailMapper {

    public FollowUpEmailDTO toDTO(FollowUpEmail email) {
        FollowUpEmailDTO dto = new FollowUpEmailDTO();
        dto.setId(email.getId());
        dto.setSubject(email.getSubject());
        dto.setBody(email.getBody());
        dto.setCreatedAt(email.getCreatedAt());
        dto.setJobId(email.getJob() != null ? email.getJob().getId() : null);
        dto.setFollowUpEmailName(email.getFollowUpEmailName());
        return dto;
    }

    public FollowUpEmail fromDTO(FollowUpEmailDTO dto, Job job) {
        FollowUpEmail email = new FollowUpEmail();
        email.setId(dto.getId());
        email.setSubject(dto.getSubject());
        email.setBody(dto.getBody());
        email.setCreatedAt(dto.getCreatedAt());
        email.setJob(job);
        return email;
    }
}
