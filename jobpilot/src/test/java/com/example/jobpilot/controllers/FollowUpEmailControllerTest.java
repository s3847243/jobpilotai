package com.example.jobpilot.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import com.example.jobpilot.followup.controller.FollowUpEmailController;
import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.dto.ImproveEmailRequest;
import com.example.jobpilot.followup.service.FollowUpEmailService;
import com.example.jobpilot.user.model.Role;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.model.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
// For MockMvc HTTP methods like get(), post(), etc.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// For status().isOk(), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = FollowUpEmailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class) // Optional: Only if you use security config
public class FollowUpEmailControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private FollowUpEmailService followUpEmailService;
    private void setAuthenticatedUser(User user) {
                UserPrincipal userPrincipal = new UserPrincipal(user);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
        }

     @Test
    void generateFollowUpEmail_shouldReturnDto() throws Exception {
        // Arrange
        UUID jobId = UUID.randomUUID();

         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); // ✅ inject into SecurityContext

        FollowUpEmailDTO followUpEmailDTO = FollowUpEmailDTO.builder()
                .id(UUID.randomUUID())
                .subject("Follow up on application")
                .body("Just checking in!")
                .build();

        Mockito.when(followUpEmailService.generateFollowUpEmail(eq(jobId), eq(user)))
                .thenReturn(followUpEmailDTO);

        // Act & Assert
        mockMvc.perform(post("/api/follow-up/generate/" + jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Follow up on application"))
                .andExpect(jsonPath("$.body").value("Just checking in!"));
    }
    @Test
    void generateFollowUpEmail_shouldReturn500_whenServiceFails() throws Exception {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); 
        UUID jobId = UUID.randomUUID();

        Mockito.when(followUpEmailService.generateFollowUpEmail(any(), any()))
                .thenThrow(new RuntimeException("Failed to generate"));

        mockMvc.perform(post("/api/follow-up/generate/" + jobId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getFollowUpById_shouldReturnFollowUpEmailDTO() throws Exception {
        UUID followUpId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); 
        FollowUpEmailDTO dto = FollowUpEmailDTO.builder()
                .id(followUpId)
                .subject("Follow Up Subject")
                .body("Follow up content")
                .build();

        when(followUpEmailService.getById(eq(followUpId), eq(user.getUserId())))
                .thenReturn(dto);

        mockMvc.perform(get("/api/follow-up/" + followUpId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(followUpId.toString()))
                .andExpect(jsonPath("$.subject").value("Follow Up Subject"))
                .andExpect(jsonPath("$.body").value("Follow up content"));
        }
    @Test
        void getFollowUpById_shouldReturn404IfNotFound() throws Exception {
            UUID followUpId = UUID.randomUUID();
            User user = User.builder()
                    .userId(UUID.randomUUID())
                    .email("test@example.com")
                    .password("password")
                    .role(Role.USER)
                    .build();
            setAuthenticatedUser(user); 

            when(followUpEmailService.getById(eq(followUpId), eq(user.getUserId())))
                    .thenThrow(new NoSuchElementException("Follow-up not found"));

            mockMvc.perform(get("/api/follow-up/" + followUpId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Follow-up not found"));
        }
    @Test
    void getAllForUser_shouldReturnListOfFollowUpEmails() throws Exception {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); 

        List<FollowUpEmailDTO> followUpEmails = List.of(
                FollowUpEmailDTO.builder()
                        .id(UUID.randomUUID())
                        .subject("Subject 1")
                        .body("Content 1")
                        .build(),
                FollowUpEmailDTO.builder()
                        .id(UUID.randomUUID())
                        .subject("Subject 2")
                        .body("Content 2")
                        .build()
        );

        when(followUpEmailService.getAllForUser(user.getUserId())).thenReturn(followUpEmails);

        mockMvc.perform(get("/api/follow-up/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].subject").value("Subject 1"))
                .andExpect(jsonPath("$[1].subject").value("Subject 2"));
    }

    @Test
    void improveFollowUpEmail_shouldReturnImprovedDTO() throws Exception {
        UUID followUpId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); 

        ImproveEmailRequest request = new ImproveEmailRequest("Make it more concise");

        FollowUpEmailDTO improved = FollowUpEmailDTO.builder()
                .id(followUpId)
                .subject("Updated Subject")
                .body("Improved Content")
                .build();
        when(followUpEmailService.improveFollowUpEmail(followUpId, user.getUserId(), "Make it more concise"))
                .thenReturn(improved);

        mockMvc.perform(put("/api/follow-up/" + followUpId + "/improve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(result -> System.out.println("RESPONSE: " + result.getResponse().getContentAsString())) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(followUpId.toString()))
                .andExpect(jsonPath("$.subject").value("Updated Subject"))
                .andExpect(jsonPath("$.body").value("Improved Content"));
    }
    @Test
    void deleteFollowUpEmail_shouldReturnSuccessMessage() throws Exception {
        UUID followUpId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); 

        doNothing().when(followUpEmailService).deleteFollowUpEmail(followUpId, user);

        mockMvc.perform(delete("/api/follow-up/" + followUpId))
                .andExpect(status().isOk())
                .andExpect(content().string("Follow-up email deleted successfully."));
    }

}
