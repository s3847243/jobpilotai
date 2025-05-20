package com.example.jobpilot.coverletter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, UUID> {

    List<CoverLetter> findAllByJobUser(User user);
    Optional<CoverLetter> findByJob(Job job);
    boolean existsByJobId(UUID jobId);
    void deleteByJobId(UUID jobId);
}