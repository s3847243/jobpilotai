package com.example.jobpilot.user.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // bcrypt encoded

    private String fullName;
    private String jobTitle;
    private String location;

    @ElementCollection
    private List<String> skills;

    private String linkedinUrl;

    private Integer resumeVersionCount = 0;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

}