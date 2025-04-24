package com.example.jobpilot.resume.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.user.model.User;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByUser(User user);
}