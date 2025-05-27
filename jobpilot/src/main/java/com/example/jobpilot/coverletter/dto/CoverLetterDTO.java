package com.example.jobpilot.coverletter.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverLetterDTO {
    private UUID id;
    private UUID userId;
    private UUID jobId;

    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    private boolean isFinalVersion;
    private String coverLetterName;
}