package com.example.jobpilot.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.user.model.User;

public interface UserRepository extends JpaRepository<User,UUID> {

    Optional<User> findByUserId(UUID userId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
}
