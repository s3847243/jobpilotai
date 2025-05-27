package com.example.jobpilot.resume.mapper;

import org.springframework.stereotype.Component;

import com.example.jobpilot.resume.dto.ResumeDTO;
import com.example.jobpilot.resume.model.Resume;

@Component
public class ResumeMapper {

    public ResumeDTO toDTO(Resume resume) {
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setFilename(resume.getFilename());
        dto.setS3Url(resume.getS3Url());
        dto.setParsedName(resume.getParsedName());
        dto.setParsedEmail(resume.getParsedEmail());
        dto.setParsedPhone(resume.getParsedPhone());
        dto.setParsedSkills(resume.getParsedSkills());
        dto.setParsedSummary(resume.getParsedSummary());
        dto.setAtsScore(resume.getAtsScore());
        dto.setUploadedAt(resume.getUploadedAt());
        dto.setUserId(resume.getUser().getUserId());
        return dto;
    }
}