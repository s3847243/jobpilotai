package com.example.jobpilot.controllers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.example.jobpilot.TestSecurityConfig;
import com.example.jobpilot.coverletter.controller.CoverLetterController;
import com.example.jobpilot.coverletter.dto.CoverLetterDTO;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.service.CoverLetterService;
import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.model.Role;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.model.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

// For MockMvc HTTP methods like get(), post(), etc.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// For status().isOk(), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// For matching JSON fields like jsonPath("$.title")
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
// For matching raw response content (e.g., empty list as "[]")
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
@WebMvcTest(CoverLetterController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class CoverLetterControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CoverLetterService coverLetterService;

        private UserPrincipal mockUserPrincipal() {
                User user = new User();
                user.setUserId(UUID.randomUUID());
                user.setEmail("test@example.com");
                user.setPassword("mock-password");

                return new UserPrincipal(user);
        }
        private void setAuthenticatedUser(User user) {
                UserPrincipal userPrincipal = new UserPrincipal(user);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
        }
        @Test
        void getAllCoverLetters_shouldReturnList() throws Exception {
        // Given
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); // ✅ inject into SecurityContext

        CoverLetterDTO dto = new CoverLetterDTO(user.getUserId(),user.getUserId(), user.getUserId(),"content",Instant.now(),Instant.now(),false,"name");

        when(coverLetterService.getAllCoverLettersByUser(user))
                .thenReturn(List.of(dto));

        // When & Then
        mockMvc.perform(get("/api/cover-letters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("content"));
        }
        @Test
        void getAllCoverLetters_shouldReturnEmptyListIfNoneExist() throws Exception {
                User user = User.builder()
                        .userId(UUID.randomUUID())
                        .email("test@example.com")
                        .password("password")
                        .role(Role.USER)
                        .build();

                setAuthenticatedUser(user); // ✅ inject into SecurityContext
                when(coverLetterService.getAllCoverLettersByUser(user))
                        .thenReturn(List.of());

                mockMvc.perform(get("/api/cover-letters"))
                        .andExpect(status().isOk())
                        .andExpect(content().json("[]"));
        }

        @Test
        void getCoverLetterById_shouldReturnCoverLetter() throws Exception {
                UUID id = UUID.randomUUID();
                User user = User.builder()
                        .userId(UUID.randomUUID())
                        .email("test@example.com")
                        .password("password")
                        .role(Role.USER)
                        .build();
                                        setAuthenticatedUser(user); // ✅ inject into SecurityContext

                CoverLetterDTO dto = new CoverLetterDTO(user.getUserId(),user.getUserId(), user.getUserId(),"content",Instant.now(),Instant.now(),false,"name");

                when(coverLetterService.getCoverLetterByIdForUser(id, user))
                        .thenReturn(dto);

                mockMvc.perform(get("/api/cover-letters/" + id))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").value("content"));
        }
        @Test
        void getCoverLetterById_shouldReturn404IfNotFound() throws Exception {
                UUID id = UUID.randomUUID();
                User user = User.builder()
                        .userId(UUID.randomUUID())
                        .email("test@example.com")
                        .password("password")
                        .role(Role.USER)
                        .build();
                                        setAuthenticatedUser(user); // ✅ inject into SecurityContext
                when(coverLetterService.getCoverLetterByIdForUser(id, user))
                        .thenThrow(new NoSuchElementException("Cover letter not found"));

                mockMvc.perform(get("/api/cover-letters/" + id))
                        .andExpect(status().isNotFound());
        }
        @Test
        void generateCoverLetter_shouldReturnGeneratedDTO() throws Exception {
                UUID coverLetterId = UUID.randomUUID();
                User user = User.builder()
                                .userId(UUID.randomUUID())
                                .email("test@example.com")
                                .password("password")
                                .role(Role.USER)
                                .build();
                                                setAuthenticatedUser(user); // ✅ inject into SecurityContext
                        CoverLetterRequest request = new CoverLetterRequest(coverLetterId,coverLetterId);
                        CoverLetterDTO dto = new CoverLetterDTO(user.getUserId(),user.getUserId(), user.getUserId(),"content",Instant.now(),Instant.now(),false,"name");
                when(coverLetterService.generateCoverLetter(eq(request), eq(user)))
                        .thenReturn(dto);

                mockMvc.perform(post("/api/cover-letters/generate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").value("content"));
        }

        @Test
        void improveCoverLetter_shouldReturnImprovedText() throws Exception {
                UUID id = UUID.randomUUID();
                User user = User.builder()
                                .userId(UUID.randomUUID())
                                .email("test@example.com")
                                .password("password")
                                .role(Role.USER)
                                .build();
                                                setAuthenticatedUser(user); // ✅ inject into SecurityContext
                String instruction = "Make it more professional";

                when(coverLetterService.improveCoverLetter(id, instruction, user))
                        .thenReturn("Improved version of the cover letter");

                mockMvc.perform(post("/api/cover-letters/" + id + "/improve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("instruction", instruction))))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Improved version of the cover letter"));
        }
        @Test
        void improveCoverLetter_shouldReturnBadRequestIfInstructionMissing() throws Exception {
                UUID id = UUID.randomUUID();

                mockMvc.perform(post("/api/cover-letters/" + id + "/improve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of()))) // Empty map
                        .andExpect(status().isBadRequest());
        }
        @Test
        void improveCoverLetter_shouldReturnBadRequestIfInstructionBlank() throws Exception {
                UUID id = UUID.randomUUID();

                mockMvc.perform(post("/api/cover-letters/" + id + "/improve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("instruction", "  ")))) // Blank string
                        .andExpect(status().isBadRequest());
        }
        @Test
        void updateCoverLetter_shouldReturnUpdatedDTO() throws Exception {
                UUID id = UUID.randomUUID();
                String newText = "Updated cover letter content";
                User user = User.builder()
                                .userId(UUID.randomUUID())
                                .email("test@example.com")
                                .password("password")
                                .role(Role.USER)
                                .build();
                setAuthenticatedUser(user); // ✅ inject into SecurityContext
                CoverLetterDTO updatedDTO = new CoverLetterDTO(user.getUserId(),user.getUserId(), user.getUserId(),"content",Instant.now(),Instant.now(),false,"name");

                when(coverLetterService.updateCoverLetter(id, newText, user))
                        .thenReturn(updatedDTO);

                mockMvc.perform(put("/api/cover-letters/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("text", newText))))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content").value("content"));
        }
        @Test
        void updateCoverLetter_shouldReturnBadRequestIfTextMissing() throws Exception {
                UUID id = UUID.randomUUID();

                mockMvc.perform(put("/api/cover-letters/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of())))
                        .andExpect(status().isBadRequest());
        }
        @Test
        void updateCoverLetter_shouldReturnBadRequestIfTextBlank() throws Exception {
                UUID id = UUID.randomUUID();

                mockMvc.perform(put("/api/cover-letters/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("text", "   "))))
                        .andExpect(status().isBadRequest());
        }
        @Test
        void deleteCoverLetter_shouldReturnSuccessMessage() throws Exception {
                User user = User.builder()
                                        .userId(UUID.randomUUID())
                                        .email("test@example.com")
                                        .password("password")
                                        .role(Role.USER)
                                        .build();
                        setAuthenticatedUser(user); // ✅ inject into SecurityContext
                mockMvc.perform(delete("/api/cover-letters/" + user.getUserId()))
                        .andExpect(status().isOk())
                        .andExpect(content().string("Cover letter deleted successfully."));
        }
        @Test
        void deleteCoverLetter_shouldReturn404IfNotFound() throws Exception {
                User user = User.builder()
                                        .userId(UUID.randomUUID())
                                        .email("test@example.com")
                                        .password("password")
                                        .role(Role.USER)
                                        .build();
                        setAuthenticatedUser(user); // ✅ inject into SecurityContext
                UUID id = UUID.randomUUID();

                doThrow(new NoSuchElementException("Cover letter not found"))
                        .when(coverLetterService).deleteCoverLetter(id, user);

                mockMvc.perform(delete("/api/cover-letters/" + id))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.error").value("Cover letter not found"));
        }

}