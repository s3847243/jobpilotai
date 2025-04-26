package com.example.jobpilot.job.dto;

import com.example.jobpilot.job.model.JobStatus;

import lombok.Data;

@Data
public class UpdateJobStatusRequest {
    private JobStatus status;
}
