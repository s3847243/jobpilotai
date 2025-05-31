package com.example.jobpilot.user.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", updatable = false, nullable = false)
    @NonNull
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; 

    private String fullName;
    private String jobTitle;
    private String location;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

}