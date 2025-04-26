package com.example.jobpilot.coverletter.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CoverLetterRequest {
    private UUID resumeId;
    private UUID jobId;
}