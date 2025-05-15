package com.example.jobpilot.coverletter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, UUID> {

    // Get all cover letters by user
    List<CoverLetter> findAllByJobUser(User user);


    // Correct: get by job
    Optional<CoverLetter> findByJob(Job job);

    // Optional: check if one exists for job
    boolean existsByJobId(UUID jobId);

    // Optional: delete by job id
    void deleteByJobId(UUID jobId);
}