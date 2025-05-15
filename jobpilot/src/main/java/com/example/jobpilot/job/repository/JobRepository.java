package com.example.jobpilot.job.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.user.model.User;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByUser(User user);
    
    @Query(value = "SELECT * FROM jobs WHERE user_id = :userId AND cover_letter IS NOT NULL", nativeQuery = true)
    List<Job> findJobsWithCoverLetterByUserNative(@Param("userId") UUID userId);
}