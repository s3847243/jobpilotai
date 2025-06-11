package com.example.jobpilot.job.model;

import java.util.UUID;

public interface JobSummaryView {
    UUID getId();
    String getTitle();
    String getCompany();
    Double getMatchScore();
    String getStatus();
    String getUrl();
}