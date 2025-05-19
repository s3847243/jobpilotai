package com.example.jobpilot.job.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String company;
    private String location;
    private String employmentType; // e.g., Full-time, Part-time, Remote

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "resume_id", nullable = true)
    @JsonManagedReference 
    private Resume resume;
    @ElementCollection
    private List<String> requiredSkills;

    private String url;
    private String source; // "LinkedIn", "Seek", "Manual", etc.

    private Double matchScore; // Calculated value
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    private Instant createdAt;

    @Column(columnDefinition = "TEXT")
    private String matchFeedback;   
    @ElementCollection
    private List<String> missingSkills;
 
    @JsonManagedReference 
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private CoverLetter coverLetter;
    
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private FollowUpEmail followUpEmail;

}