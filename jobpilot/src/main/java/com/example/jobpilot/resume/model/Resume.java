package com.example.jobpilot.resume.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String filename;
    private String s3Url;
    private String parsedName;
    private String parsedEmail;
    private String parsedPhone;
    private Double atsScore;
    @ElementCollection
    private List<String> parsedSkills;
    @Column(columnDefinition = "TEXT")
    private String parsedSummary;
    private Instant uploadedAt;
    @OneToMany(mappedBy = "resume")
    private List<Job> jobs;
}