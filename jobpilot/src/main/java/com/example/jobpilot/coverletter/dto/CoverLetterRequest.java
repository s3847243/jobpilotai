package com.example.jobpilot.coverletter.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoverLetterRequest {
    private UUID resumeId;
    private UUID jobId;
}