package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.jobpilot.auth.dto.AuthResponse;
import com.example.jobpilot.auth.dto.LoginRequest;
import com.example.jobpilot.auth.dto.RegisterRequest;
import com.example.jobpilot.auth.model.RefreshToken;
import com.example.jobpilot.auth.service.AuthService;
import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.auth.service.RefreshTokenService;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
        @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;
@Test
void register_shouldCreateUserAndReturnTokens() {
    RegisterRequest request = new RegisterRequest("test@example.com", "password", "John Doe", "Developer", "NY");

    User mockUser = User.builder()
        .userId(UUID.randomUUID())
        .email(request.getEmail())
        .fullName(request.getFullName())
        .jobTitle(request.getJobTitle())
        .location(request.getLocation())
        .build();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
    when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

    RefreshToken mockRefreshToken = RefreshToken.builder()
        .id(1L)
        .user(mockUser)
        .token("refreshToken")
        .expiryDate(Instant.now().plusSeconds(3600))
        .build();
    when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(mockRefreshToken);

    AuthResponse response = authService.register(request);

    assertEquals("jwtToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
    verify(userRepository).save(any(User.class));
}

@Test
void register_shouldThrowIfEmailExists() {
    RegisterRequest request = new RegisterRequest("test@example.com", "password", "John Doe", "Developer", "NY");
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

    assertThrows(RuntimeException.class, () -> authService.register(request));
    verify(userRepository, never()).save(any());
}

@Test
void login_shouldAuthenticateAndReturnTokens() {
    LoginRequest request = new LoginRequest("test@example.com", "password");
    User user = User.builder()
            .userId(UUID.randomUUID())
            .email("test@example.com")
            .password("encodedPassword")
            .fullName("John Doe")
            .jobTitle("Developer")
            .location("NY")
            .build();

    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
    when(jwtService.generateToken(user)).thenReturn("jwtToken");

    RefreshToken mockRefreshToken = RefreshToken.builder()
            .id(1L)
            .user(user)
            .token("refreshToken")
            .expiryDate(Instant.now().plusSeconds(3600))
            .build();
    when(refreshTokenService.createRefreshToken(user)).thenReturn(mockRefreshToken);

    
    AuthResponse response = authService.login(request);

    assertEquals("jwtToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
}

@Test
void login_shouldThrowIfUserNotFound() {
    // Arrange
    LoginRequest request = new LoginRequest("test@example.com", "password");
    when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> authService.login(request));
}


}