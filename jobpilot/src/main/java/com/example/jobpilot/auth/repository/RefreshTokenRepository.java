package com.example.jobpilot.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jobpilot.auth.model.RefreshToken;
import com.example.jobpilot.user.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);

}
