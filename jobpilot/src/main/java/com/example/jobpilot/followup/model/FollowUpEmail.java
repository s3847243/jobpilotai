package com.example.jobpilot.followup.model;

import java.time.Instant;
import java.util.UUID;

import com.example.jobpilot.job.model.Job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowUpEmail {
    
    @Id
    @GeneratedValue
    private UUID id;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    private Instant createdAt;

    @OneToOne
    @JoinColumn(name = "job_id", nullable = false, unique = true)
    private Job job;

    private String followUpEmailName;

}