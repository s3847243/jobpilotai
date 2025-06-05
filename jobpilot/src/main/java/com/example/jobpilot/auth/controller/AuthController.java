package com.example.jobpilot.auth.controller;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.jobpilot.auth.dto.AuthResponse;
import com.example.jobpilot.auth.dto.LoginRequest;
import com.example.jobpilot.auth.dto.RegisterRequest;
import com.example.jobpilot.auth.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    AuthResponse authResponse = authService.login(request); 

    // Set Access Token
    Cookie accessTokenCookie = new Cookie("accessToken", authResponse.getAccessToken());
    accessTokenCookie.setHttpOnly(true);
    accessTokenCookie.setSecure(false); // Only on HTTPS
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(15 * 60); 

    // Set Refresh Token
    Cookie refreshTokenCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); 
    response.setHeader("Set-Cookie",
    "accessToken=" + authResponse.getAccessToken() +
    "; HttpOnly; Path=/; Max-Age=900; SameSite=Lax");

    response.setHeader("Set-Cookie",
        "refreshToken=" + authResponse.getRefreshToken() +
        "; HttpOnly; Path=/; Max-Age=" + (7 * 24 * 60 * 60) + "; SameSite=Lax");
    response.addCookie(accessTokenCookie);
    response.addCookie(refreshTokenCookie);

    return ResponseEntity.ok(Map.of(
        "user", authResponse.getUser() 
    ));
}
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

}
