package com.example.jobpilot.job.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByUser(User user);
}