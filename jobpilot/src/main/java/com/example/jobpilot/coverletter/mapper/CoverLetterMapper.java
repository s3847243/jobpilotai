package com.example.jobpilot.coverletter.mapper;

import org.springframework.stereotype.Component;

import com.example.jobpilot.coverletter.dto.CoverLetterDTO;
import com.example.jobpilot.coverletter.model.CoverLetter;

@Component
public class CoverLetterMapper {

    public CoverLetterDTO toDTO(CoverLetter entity) {
        CoverLetterDTO dto = new CoverLetterDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getUserId());
        dto.setJobId(entity.getJob().getId());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setFinalVersion(entity.isFinalVersion());
        return dto;
    }

}