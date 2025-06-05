package com.example.jobpilot.controllers;



import com.example.jobpilot.TestSecurityConfig;
import com.example.jobpilot.auth.controller.AuthController;
import com.example.jobpilot.auth.dto.AuthResponse;
import com.example.jobpilot.auth.dto.LoginRequest;
import com.example.jobpilot.auth.dto.RegisterRequest;
import com.example.jobpilot.auth.service.AuthService;
import com.example.jobpilot.user.dto.UserDTO;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)  // disables Spring Security
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void login_shouldReturnUserAndSetCookies() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        UserDTO userDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        AuthResponse authResponse = new AuthResponse(
                "access-token-123",
                "refresh-token-456",
                userDTO
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // when + then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(cookie().value("accessToken", "access-token-123"))
                .andExpect(cookie().value("refreshToken", "refresh-token-456"));
    }
    @Test
    void register_shouldReturnAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password", "Test User","engineer","melbourne");

        UserDTO userDTO = UserDTO.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .fullName("Test User")
            .build();

        AuthResponse response = new AuthResponse("access-token-abc", "refresh-token-def", userDTO);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token-abc"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token-def"))
            .andExpect(jsonPath("$.user.fullName").value("Test User"));
    }

    @Test
    void login_shouldReturn401IfAuthFails() throws Exception {
        LoginRequest request = new LoginRequest("wrong@example.com", "wrongpass");

        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
}
