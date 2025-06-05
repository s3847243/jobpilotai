package com.example.jobpilot.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.jobpilot.TestSecurityConfig;
import com.example.jobpilot.user.controller.UserController;
import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.dto.UserProfileUpdateRequest;
import com.example.jobpilot.user.model.Role;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.model.UserPrincipal;
import com.example.jobpilot.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
// For MockMvc HTTP methods like get(), post(), etc.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// For status().isOk(), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class) 
public class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserService userService;
    private void setAuthenticatedUser(User user) {
                UserPrincipal userPrincipal = new UserPrincipal(user);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
    }
    @Test
    void updateProfile_shouldReturnUpdatedUserDTO() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); 

        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setName("Updated Name");
        request.setJobTitle("Engineer");

        UserDTO updatedDto = UserDTO.builder()
            .id(userId)
            .email("test@example.com")
            .fullName("Updated Name")
            .jobTitle("Engineer")
            .build();

        when(userService.updateProfile(eq(user), any(UserProfileUpdateRequest.class)))
            .thenReturn(updatedDto);

        mockMvc.perform(patch("/api/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Updated Name"))
            .andExpect(jsonPath("$.jobTitle").value("Engineer"));
    }

    @Test
    void deleteAccount_shouldReturnNoContent_whenAuthenticated() throws Exception {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); 

        doNothing().when(userService).deleteAccount(user);

        mockMvc.perform(delete("/api/user/delete"))
            .andExpect(status().isNoContent());
    }
    @Test
    void deleteAccount_shouldReturn401_whenUserPrincipalIsNull() throws Exception {
        mockMvc.perform(delete("/api/user/delete"))
            .andExpect(status().isUnauthorized());
    }
}
