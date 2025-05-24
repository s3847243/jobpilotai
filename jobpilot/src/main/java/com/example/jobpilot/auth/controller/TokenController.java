package com.example.jobpilot.auth.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
public ResponseEntity<AuthResponse> refreshToken(@CookieValue(value = "refreshToken", required = false) String requestRefreshToken) {
    System.out.println("somethign happens here");
    if (requestRefreshToken == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    RefreshToken token = refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    User user = token.getUser();
    String newAccessToken = jwtService.generateToken(user);

    // Optionally, refresh token can be rotated and re-set here

    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(true) // ✅ required in production
            .path("/")
            .sameSite("Lax") // or "None" if cross-site
            .maxAge(Duration.ofMinutes(15))
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .body(AuthResponse.builder()
                    .accessToken(newAccessToken) // optional, mostly for dev
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