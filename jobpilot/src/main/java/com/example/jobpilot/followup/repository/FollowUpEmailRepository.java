package com.example.jobpilot.followup.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.followup.model.FollowUpEmail;

public interface FollowUpEmailRepository extends JpaRepository<FollowUpEmail,UUID>{
    Optional<FollowUpEmail> findByIdAndJobUserId(Long id, Long userId);
    List<FollowUpEmail> findAllByJobUserId(Long userId);
}   
