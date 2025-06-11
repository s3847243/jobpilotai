package com.example.jobpilot.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.jobpilot.TestSecurityConfig;
import com.example.jobpilot.job.dto.JobSummaryDTO;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.controller.ResumeController;
import com.example.jobpilot.resume.dto.ResumeDTO;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.Role;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.model.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.mock.web.MockMultipartFile;
@WebMvcTest(ResumeController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ResumeControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private ResumeService resumeService;
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
    void uploadResume_shouldReturnResumeDTO() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
            "file", "resume.pdf", "application/pdf", "Test Resume Content".getBytes()
        );
         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); 
        ResumeDTO mockResumeDTO = ResumeDTO.builder()
            .id(UUID.randomUUID())
            .filename("resume.pdf")
            .s3Url("https://s3-bucket-url/resume.pdf")
            .build();

        when(resumeService.uploadResume(any(MultipartFile.class), eq(user))).thenReturn(mockResumeDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/resume/upload")
                .file(mockFile))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.filename").value("resume.pdf"))
            .andExpect(jsonPath("$.s3Url").value("https://s3-bucket-url/resume.pdf"));
    }
    @Test
    void deleteResume_shouldReturnSuccessMessage() throws Exception {
        UUID resumeId = UUID.randomUUID();
         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();


        setAuthenticatedUser(user); 
        mockMvc.perform(delete("/api/resume/{resumeId}", resumeId))
                    .andExpect(status().isOk())
            .andExpect(content().string("Resume deleted successfully"));

        Mockito.verify(resumeService).deleteResume(resumeId, user);
    }
    @Test
    void getAllResumes_shouldReturnResumeList() throws Exception {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); 
        ResumeDTO resume1 = ResumeDTO.builder()
                .id(UUID.randomUUID())
                .filename("resume1.pdf")
                .build();
         
        ResumeDTO resume2 = ResumeDTO.builder()
                .id(UUID.randomUUID())
                .filename("resume2.pdf")
                .build();

        List<ResumeDTO> resumes = List.of(resume1, resume2);

        Mockito.when(resumeService.getResumesByUser(user)).thenReturn(resumes);

        mockMvc.perform(get("/api/resume"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].filename").value("resume1.pdf"))
            .andExpect(jsonPath("$[1].filename").value("resume2.pdf"));
    }
    @Test
    void getResume_shouldReturnResumeById() throws Exception {
        UUID resumeId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); 
        ResumeDTO resumeDTO = ResumeDTO.builder()
                .id(resumeId)
                .filename("resume.pdf")
                .build();

        Mockito.when(resumeService.getResumeByIdForUser(eq(resumeId), eq(user))).thenReturn(resumeDTO);

        mockMvc.perform(get("/api/resume/{resumeId}", resumeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(resumeId.toString()))
            .andExpect(jsonPath("$.filename").value("resume.pdf"));
    }


}   
