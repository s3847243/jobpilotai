package com.example.jobpilot.coverletter.model;

import java.time.Instant;
import java.util.UUID;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cover_letters")
public class CoverLetter {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", unique = true, nullable = false)
    private Job job;
    @Column(length = 5000)
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isFinalVersion; 
    private String coverLetterName;

}
