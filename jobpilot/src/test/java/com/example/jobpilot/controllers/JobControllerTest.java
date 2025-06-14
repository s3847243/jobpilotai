package com.example.jobpilot.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
import com.example.jobpilot.job.controller.JobController;
import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.dto.UpdateJobStatusRequest;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.model.JobStatus;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.Role;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.model.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class JobControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JobService jobService;
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
    void addFromUrl_shouldReturnJobDTO() throws Exception {
        String url = "https://jobs.example.com/job123";
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); // ✅ inject into SecurityContext
        UUID resumeId = UUID.randomUUID();
        JobDTO jobDTO = new JobDTO(UUID.randomUUID(), "Software Engineer", "Company", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(),resumeId,resumeId,resumeId);

        when(jobService.addJobFromUrl(url, user, resumeId)).thenReturn(jobDTO);

        mockMvc.perform(post("/api/job/from-url")
                        .param("url", url)
                        .param("resumeId", resumeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Software Engineer"))
                .andExpect(jsonPath("$.company").value("Company"))
                .andExpect(jsonPath("$.url").value(url));
    }
    @Test
    void addFromUrl_shouldWorkWithoutResumeId() throws Exception {
        String url = "https://jobs.example.com/job456";
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); // ✅ inject into SecurityContext
        JobDTO jobDTO = new JobDTO(UUID.randomUUID(), "Backend Dev", "Company", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(), UUID.randomUUID(),  UUID.randomUUID(), UUID.randomUUID());

        when(jobService.addJobFromUrl(url, user, null)).thenReturn(jobDTO);

        mockMvc.perform(post("/api/job/from-url")
                        .param("url", url)) // no resumeId param
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Dev"))
                .andExpect(jsonPath("$.company").value("Company"))
                .andExpect(jsonPath("$.url").value(url));
    }
    @Test
    void addFromUrl_shouldReturn500IfServiceThrowsIOException() throws Exception {
        String url = "https://invalid.jobs/job";
        UUID resumeId = UUID.randomUUID();
            User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        setAuthenticatedUser(user); // ✅ inject into SecurityContext
        when(jobService.addJobFromUrl(url, user, resumeId))
            .thenThrow(new RuntimeException("Failed to fetch"));
            mockMvc.perform(post("/api/job/from-url")
                        .param("url", url)
                        .param("resumeId", resumeId.toString()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Failed to fetch"));
        }
    @Test
    void matchJob_shouldReturnUpdatedJobDTO() throws Exception {
        UUID jobId = UUID.randomUUID();
         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
                String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user); // ✅ inject into SecurityContext
        Resume resume = Resume.builder()
                .id(UUID.randomUUID())
                .user(user)
                .filename("Sample resume")
                .build();

        Job job = Job.builder()
                .id(jobId)
                .user(user)
                .resume(resume)
                .title("Software Engineer")
                .build();

        JobDTO jobDTO = new JobDTO(UUID.randomUUID(), "Backend Dev", "Company", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(), UUID.randomUUID(),  UUID.randomUUID(), UUID.randomUUID());

        when(jobService.getJobEntityById(jobId, user)).thenReturn(job);
        when(jobService.matchJobWithResume(job, resume)).thenReturn(jobDTO);

        mockMvc.perform(get("/api/job/" + jobId + "/match"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Backend Dev"));
    }
    @Test
    void matchJob_shouldReturn500IfResumeIsNull() throws Exception {
        UUID jobId = UUID.randomUUID();
         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
                String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user); // 
        Job job = Job.builder()
                .id(jobId)
                .user(user)
                .resume(null) // No resume assigned
                .title("Unassigned Job")
                .build();

        when(jobService.getJobEntityById(jobId, user)).thenReturn(job);

        mockMvc.perform(get("/api/job/" + jobId + "/match"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("This job has no resume assigned"));

    }
    @Test
    void listJobs_shouldReturnListOfJobDTOs() throws Exception {
         User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
                String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user); // 
        JobDTO job1 = new JobDTO(UUID.randomUUID(), "Dev 1", "Company", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(), UUID.randomUUID(),  UUID.randomUUID(), UUID.randomUUID());
        JobDTO job2 = new JobDTO(UUID.randomUUID(), "software eng", "Meta", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(), UUID.randomUUID(),  UUID.randomUUID(), UUID.randomUUID());

        when(jobService.getUserJobs(user)).thenReturn(List.of(job1, job2));

        mockMvc.perform(get("/api/job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dev 1"))
                .andExpect(jsonPath("$[1].company").value("Meta"));
    }
    @Test
    void listJobs_shouldReturnEmptyListWhenNoneExist() throws Exception {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
                String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user); // 
        when(jobService.getUserJobs(user)).thenReturn(List.of());

        mockMvc.perform(get("/api/job"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
    @Test
    void getJobById_shouldReturnJobDTO() throws Exception {
        UUID jobId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
                String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user); // 
        JobDTO job1 = new JobDTO(UUID.randomUUID(), "Dev 1", "Company", "Description", url,JobStatus.APPLIED,"feedback",new ArrayList<String>(),85.0,user.getUserId(), UUID.randomUUID(),  UUID.randomUUID(), UUID.randomUUID());

        when(jobService.getJobById(jobId, user)).thenReturn(job1);

        mockMvc.perform(get("/api/job/" + jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dev 1"))
                .andExpect(jsonPath("$.company").value("Company"))
                .andExpect(jsonPath("$.url").value("https://jobs.example.com/job456"));
    }
    @Test
    void getJobById_shouldReturn404IfJobNotFound() throws Exception {
        UUID jobId = UUID.randomUUID();
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        setAuthenticatedUser(user); // 
        when(jobService.getJobById(jobId, user))
                .thenThrow(new NoSuchElementException("Job not found"));

        mockMvc.perform(get("/api/job/" + jobId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Job not found"));
    }
    @Test
    void updateStatus_shouldReturnUpdatedJobDTO() throws Exception {
        UUID jobId = UUID.randomUUID();
        User user = User.builder()
                    .userId(UUID.randomUUID())
                    .email("test@example.com")
                    .password("password")
                    .role(Role.USER)
                    .build();
        String url = "https://jobs.example.com/job456";

        setAuthenticatedUser(user);  
        
        UpdateJobStatusRequest request = new UpdateJobStatusRequest();
        request.setStatus(JobStatus.SAVED);  // Set to SAVED

        // Mock the return with SAVED status
        JobDTO job1 = new JobDTO(UUID.randomUUID(), "developer", "Company", "Description", 
                                url, JobStatus.SAVED, "feedback", new ArrayList<String>(), 
                                85.0, user.getUserId(), UUID.randomUUID(), 
                                UUID.randomUUID(), UUID.randomUUID());

        // Mock should match what you're sending in request
        when(jobService.updateJobStatus(jobId, "SAVED", user)).thenReturn(job1);

        mockMvc.perform(patch("/api/job/" + jobId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("developer"))
                .andExpect(jsonPath("$.status").value("SAVED"));
    }
    @Test
    void assignResumeToJob_shouldReturnUpdatedJobDTO() throws Exception {
        UUID jobId = UUID.randomUUID();
        UUID resumeId = UUID.randomUUID();
        User user = User.builder()
                    .userId(UUID.randomUUID())
                    .email("test@example.com")
                    .password("password")
                    .role(Role.USER)
                    .build();

        setAuthenticatedUser(user);  
        JobDTO updated = JobDTO.builder()
            .id(jobId)
            .title("Backend Engineer")
            .company("Spotify")
            .url("https://spotify.com/job")
            .status(JobStatus.APPLIED)
            .build();

        when(jobService.assignResume(eq(jobId), eq(resumeId), eq(user)))
            .thenReturn(updated);

        mockMvc.perform(put("/api/job/" + jobId + "/assign-resume/" + resumeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Backend Engineer"))
            .andExpect(jsonPath("$.company").value("Spotify"))
            .andExpect(jsonPath("$.status").value("APPLIED"));
    }
    @Test
    void deleteJobById_shouldReturnSuccessMessage() throws Exception {
        UUID jobId = UUID.randomUUID();
        User user = User.builder()
                    .userId(UUID.randomUUID())
                    .email("test@example.com")
                    .password("password")
                    .role(Role.USER)
                    .build();

        setAuthenticatedUser(user);  
        // No need to mock return since the method is void, just verify later
        doNothing().when(jobService).deleteJobById(eq(jobId), eq(user));

        mockMvc.perform(delete("/api/job/" + jobId))
            .andExpect(status().isOk())
            .andExpect(content().string("Job deleted successfully."));

        // Optional: verify interaction
        verify(jobService).deleteJobById(eq(jobId), eq(user));
    }


}
