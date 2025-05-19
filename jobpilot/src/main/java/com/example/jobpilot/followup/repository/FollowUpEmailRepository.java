package com.example.jobpilot.followup.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.jobpilot.followup.model.FollowUpEmail;

public interface FollowUpEmailRepository extends JpaRepository<FollowUpEmail,UUID>{
 @Query("SELECT f FROM FollowUpEmail f WHERE f.id = :id AND f.job.user.userId = :userId")
    Optional<FollowUpEmail> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT f FROM FollowUpEmail f WHERE f.job.user.userId = :userId")
    List<FollowUpEmail> findAllByUserId(@Param("userId") UUID userId);
}   
