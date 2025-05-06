package com.example.jobpilot.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.auth.dto.AuthResponse;
import com.example.jobpilot.auth.dto.TokenRefreshRequest;
import com.example.jobpilot.auth.model.RefreshToken;
import com.example.jobpilot.user.repository.UserRepository;
import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.auth.service.RefreshTokenService;
import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    RefreshToken token = refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    User user = token.getUser();
    String newAccessToken = jwtService.generateToken(user);

    return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(requestRefreshToken)
            .user(UserDTO.from(user))
            .build());
}
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractEmail(jwt);

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.revokeByUserId(user.getUserId());

        return ResponseEntity.ok("User logged out successfully");
    }
}