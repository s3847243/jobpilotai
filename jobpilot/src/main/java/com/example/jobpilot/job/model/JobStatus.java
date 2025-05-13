package com.example.jobpilot.job.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum JobStatus {
    SAVED,
    APPLIED,
    REJECTED;

    @JsonCreator
    public static JobStatus fromString(String value) {
        return JobStatus.valueOf(value.toUpperCase());
    }
}
