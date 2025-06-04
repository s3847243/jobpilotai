package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.repository.CoverLetterRepository;
import com.example.jobpilot.job.dto.JobDTO;
import com.example.jobpilot.job.mapper.JobMapper;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.model.JobStatus;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.job.service.JobService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.User;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock private JobRepository jobRepository;
    @Mock private OpenAiService openAiService;
    @Mock private ResumeService resumeService;
    @Mock private ResumeRepository resumeRepository;
    @Mock private CoverLetterRepository coverLetterRepository;
    @Mock private JobMapper jobMapper;

    @InjectMocks private JobService jobService;

    @Test
    void addJobFromUrl_shouldCreateJobWithResume() throws Exception {
        String url = "http://example.com/job";
        User user = new User();
        user.setUserId(UUID.randomUUID());

        UUID resumeId = UUID.randomUUID();
        Resume resume = new Resume();
        when(resumeService.getResumeEntityByIdForUser(resumeId)).thenReturn(resume);

        // Mocked job JSON from AI
        String jobJson = """
            {
                "title": "Software Engineer",
                "company": "Tech Corp",
                "location": "Remote",
                "employmentType": "Full-time",
                "description": "Exciting job!"
            }
        """;

        // ✅ Fix: Create Document BEFORE the try block and mock the chain properly
        Document document = Jsoup.parse("<html><body>Sample job description</body></html>");
        
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            Connection connection = mock(Connection.class);

            // ✅ Mock the entire chain to return the same connection instance
            jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connection);
            when(connection.userAgent("Mozilla")).thenReturn(connection);
            when(connection.timeout(10_000)).thenReturn(connection);
            when(connection.get()).thenReturn(document);  // ✅ Return the actual Document

            when(openAiService.extractJobInfoFromText(anyString())).thenReturn(jobJson);

            Job savedJob = Job.builder()
                    .title("Software Engineer")
                    .company("Tech Corp")
                    .user(user)
                    .resume(resume)
                    .build();

            when(jobRepository.save(any())).thenReturn(savedJob);
            when(jobMapper.toDTO(any())).thenReturn(new JobDTO());

            JobDTO result = jobService.addJobFromUrl(url, user, resumeId);

            assertNotNull(result);
            verify(openAiService).extractJobInfoFromText("Sample job description");
            verify(resumeService).getResumeEntityByIdForUser(resumeId);
            verify(jobRepository).save(any(Job.class));
            verify(jobMapper).toDTO(any(Job.class));
        }
    }
@Test
void addJobFromUrl_shouldThrowExceptionWhenOpenAiFails() throws Exception {
    String url = "https://example.com/job";
    User user = User.builder().userId(UUID.randomUUID()).build();

    // ✅ Mock a document with non-empty body text
    Document doc = Jsoup.parse("<html><body>Some job content</body></html>");

    try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
        Connection connection = mock(Connection.class);
        
        // ✅ Mock the COMPLETE chain - this was missing!
        jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connection);
        when(connection.userAgent("Mozilla")).thenReturn(connection);
        when(connection.timeout(10_000)).thenReturn(connection);
        when(connection.get()).thenReturn(doc);

        // ✅ Expect OpenAI to be called and throw
        when(openAiService.extractJobInfoFromText(anyString()))
                .thenThrow(new RuntimeException("OpenAI error"));

        assertThrows(RuntimeException.class, () -> jobService.addJobFromUrl(url, user, null));

        verify(openAiService).extractJobInfoFromText(anyString());
        verify(jobRepository, never()).save(any());
    }
}

    @Test
void addJobFromUrl_shouldThrowExceptionWhenResumeNotFound() throws Exception {
    String url = "https://example.com/job";
    UUID resumeId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();

    Document doc = Jsoup.parse("<html><body>Test page</body></html>");
    
    try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
        Connection connection = mock(Connection.class);
        
        // ✅ Mock the complete chain properly
        jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connection);
        when(connection.userAgent("Mozilla")).thenReturn(connection);
        when(connection.timeout(10_000)).thenReturn(connection);
        when(connection.get()).thenReturn(doc);

        when(openAiService.extractJobInfoFromText(anyString()))
                .thenReturn("{\"title\":\"Backend Engineer\",\"company\":\"Acme Inc\",\"location\":\"Remote\",\"employmentType\":\"Full-time\",\"description\":\"Exciting role!\"}");

        when(resumeService.getResumeEntityByIdForUser(any())).thenThrow(new RuntimeException("Resume not found"));

        assertThrows(RuntimeException.class, () -> jobService.addJobFromUrl(url, user, resumeId));

        verify(resumeService).getResumeEntityByIdForUser(resumeId);
        verify(jobRepository, never()).save(any());
    }
}
@Test
void addJobFromUrl_shouldThrowExceptionWhenOpenAiReturnsInvalidJson() throws Exception {
    String url = "https://example.com/job";
    User user = User.builder().userId(UUID.randomUUID()).build();

    Document doc = Jsoup.parse("<html><body>Test page</body></html>");
    
    try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
        Connection connection = mock(Connection.class);
        
        // ✅ Mock the complete chain properly
        jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connection);
        when(connection.userAgent("Mozilla")).thenReturn(connection);
        when(connection.timeout(10_000)).thenReturn(connection);
        when(connection.get()).thenReturn(doc);

        when(openAiService.extractJobInfoFromText(anyString()))
                .thenReturn("not a json");

        assertThrows(RuntimeException.class, () -> jobService.addJobFromUrl(url, user, null));

        verify(jobRepository, never()).save(any());
    }
}
    @Test
    void addJobFromUrl_shouldThrowWhenJsoupFails() throws Exception {
        String url = "https://invalid-url";
        User user = new User();
        UUID resumeId = UUID.randomUUID();

        Connection connectionMock = mock(Connection.class);
        when(connectionMock.userAgent(anyString())).thenReturn(connectionMock);
        when(connectionMock.timeout(anyInt())).thenReturn(connectionMock);
        when(connectionMock.get()).thenThrow(new IOException("Failed to fetch"));

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connectionMock);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                jobService.addJobFromUrl(url, user, resumeId);
            });

            assertTrue(ex.getMessage().contains("Failed to extract job from URL"));
        }
    }
    @Test
    void addJobFromUrl_shouldThrowWhenJsonParsingFails() throws Exception {
        String url = "https://example.com/job";
        User user = new User();
        UUID resumeId = UUID.randomUUID();

        Document doc = Jsoup.parse("<html><body>Job description here</body></html>");
        Connection connectionMock = mock(Connection.class);
        when(connectionMock.userAgent(anyString())).thenReturn(connectionMock);
        when(connectionMock.timeout(anyInt())).thenReturn(connectionMock);
        when(connectionMock.get()).thenReturn(doc);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(url)).thenReturn(connectionMock);

            when(openAiService.extractJobInfoFromText(anyString()))
                .thenReturn("Invalid JSON format");

            assertThrows(RuntimeException.class, () -> {
                jobService.addJobFromUrl(url, user, resumeId);
            });
        }

    }

    @Test
    void matchJobWithResume_shouldUpdateJobWithScoreAndSkills() {
        Job job = Job.builder().description("Job desc").build();
        Resume resume = Resume.builder().parsedSummary("Resume summary").build();

        String aiFeedback = """
            Match Score: 85
            Missing Skills: Java, Spring Boot
            """;

        when(openAiService.getMatchExplanation(anyString(), anyString())).thenReturn(aiFeedback);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toDTO(any(Job.class))).thenReturn(new JobDTO());

        JobDTO result = jobService.matchJobWithResume(job, resume);

        assertNotNull(result);
        assertEquals(85.0, job.getMatchScore());
        assertEquals(List.of("Java", "Spring Boot"), job.getMissingSkills());
        assertEquals(aiFeedback, job.getMatchFeedback());

        verify(jobRepository).save(job);
    }

    @Test
    void matchJobWithResume_shouldHandleMissingScore() {
        Job job = Job.builder().description("Job desc").build();
        Resume resume = Resume.builder().parsedSummary("Summary").build();

        String aiFeedback = "No match score available. Missing Skills: Docker, Kubernetes";

        when(openAiService.getMatchExplanation(anyString(), anyString())).thenReturn(aiFeedback);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toDTO(any(Job.class))).thenReturn(new JobDTO());

        JobDTO result = jobService.matchJobWithResume(job, resume);

        assertNotNull(result);
        assertNull(job.getMatchScore());
        assertEquals(List.of("Docker", "Kubernetes"), job.getMissingSkills());
    }

    @Test
    void matchJobWithResume_shouldHandleNoMissingSkills() {
        Job job = Job.builder().description("Job desc").build();
        Resume resume = Resume.builder().parsedSummary("Summary").build();

        String aiFeedback = "Match Score: 75";

        when(openAiService.getMatchExplanation(anyString(), anyString())).thenReturn(aiFeedback);
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jobMapper.toDTO(any(Job.class))).thenReturn(new JobDTO());

        JobDTO result = jobService.matchJobWithResume(job, resume);

        assertNotNull(result);
        assertEquals(75.0, job.getMatchScore());
        assertTrue(job.getMissingSkills().isEmpty());
    }
    @Test
    void getJobEntityById_shouldReturnJobWhenAuthorized() {
        UUID jobId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();
        Job job = Job.builder().id(jobId).user(user).build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        Job result = jobService.getJobEntityById(jobId, user);

        assertNotNull(result);
        assertEquals(job, result);
    }
    @Test
    void getJobEntityById_shouldThrowWhenUnauthorized() {
        UUID jobId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();
        User otherUser = User.builder().userId(UUID.randomUUID()).build();
        Job job = Job.builder().id(jobId).user(otherUser).build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> jobService.getJobEntityById(jobId, user));

        assertEquals("Unauthorized", ex.getMessage());
    }
    @Test
    void getJobEntityById_shouldThrowWhenJobNotFound() {
        UUID jobId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> jobService.getJobEntityById(jobId, user));

        assertEquals("Job not found", ex.getMessage());
    }

    @Test
    void getUserJobs_shouldReturnJobDTOList() {
        User user = User.builder().userId(UUID.randomUUID()).build();

        Job job1 = Job.builder().id(UUID.randomUUID()).user(user).build();
        Job job2 = Job.builder().id(UUID.randomUUID()).user(user).build();

        JobDTO jobDTO1 = JobDTO.builder().id(job1.getId()).build();
        JobDTO jobDTO2 = JobDTO.builder().id(job2.getId()).build();

        when(jobRepository.findByUser(user)).thenReturn(List.of(job1, job2));
        when(jobMapper.toDTO(job1)).thenReturn(jobDTO1);
        when(jobMapper.toDTO(job2)).thenReturn(jobDTO2);

        List<JobDTO> result = jobService.getUserJobs(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(jobDTO1));
        assertTrue(result.contains(jobDTO2));
    }

    @Test
void getUserJobs_shouldReturnEmptyListWhenNoJobs() {
    User user = User.builder().userId(UUID.randomUUID()).build();

    when(jobRepository.findByUser(user)).thenReturn(List.of());

    List<JobDTO> result = jobService.getUserJobs(user);

    assertNotNull(result);
    assertTrue(result.isEmpty());
}

@Test
void getJobById_shouldReturnJobDTO_whenUserOwnsJob() {
    UUID jobId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();
    Job job = Job.builder().id(jobId).user(user).build();
    JobDTO jobDTO = JobDTO.builder().id(jobId).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(jobMapper.toDTO(job)).thenReturn(jobDTO);

    JobDTO result = jobService.getJobById(jobId, user);

    assertEquals(jobDTO, result);
}


@Test
void getJobById_shouldThrowException_whenJobNotFound() {
    UUID jobId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        jobService.getJobById(jobId, user);
    });

    assertEquals("Job not found", ex.getMessage());
}



@Test
void getJobById_shouldThrowException_whenUserUnauthorized() {
    UUID jobId = UUID.randomUUID();
    User owner = User.builder().userId(UUID.randomUUID()).build();
    User attacker = User.builder().userId(UUID.randomUUID()).build();
    Job job = Job.builder().id(jobId).user(owner).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        jobService.getJobById(jobId, attacker);
    });

    assertEquals("Unauthorized", ex.getMessage());
}

@Test
void updateJobStatus_shouldUpdateStatus_whenValid() {
    UUID jobId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();
    Job job = Job.builder().id(jobId).user(user).build();
    JobDTO expectedDto = JobDTO.builder().id(jobId).status(JobStatus.APPLIED).build();
    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(jobRepository.save(any(Job.class))).thenReturn(job);
    when(jobMapper.toDTO(job)).thenReturn(expectedDto);

    JobDTO result = jobService.updateJobStatus(jobId, "applied", user);

    assertEquals(JobStatus.APPLIED, result.getStatus());
    verify(jobRepository).save(job);
}

@Test
void updateJobStatus_shouldThrowException_whenJobNotFound() {
    UUID jobId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        jobService.updateJobStatus(jobId, "applied", user);
    });

    assertEquals("Job not found", ex.getMessage());
}

@Test
void updateJobStatus_shouldThrowException_whenUserUnauthorized() {
    UUID jobId = UUID.randomUUID();
    User owner = User.builder().userId(UUID.randomUUID()).build();
    User attacker = User.builder().userId(UUID.randomUUID()).build();
    Job job = Job.builder().id(jobId).user(owner).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        jobService.updateJobStatus(jobId, "applied", attacker);
    });

    assertEquals("Unauthorized to update this job", ex.getMessage());
}
@Test
void updateJobStatus_shouldThrowException_whenInvalidStatus() {
    UUID jobId = UUID.randomUUID();
    User user = User.builder().userId(UUID.randomUUID()).build();
    Job job = Job.builder().id(jobId).user(user).build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        jobService.updateJobStatus(jobId, "invalid_status", user);
    });

    assertTrue(ex.getMessage().startsWith("Invalid status"));
}

@Test
void replaceResume_shouldReplaceOldResumeAndUpdateJob() {
    UUID jobId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setUserId(userId);

    Resume oldResume = new Resume();
    oldResume.setId(UUID.randomUUID());

    Resume newResume = new Resume();
    newResume.setId(UUID.randomUUID());

    Job job = Job.builder()
            .id(jobId)
            .user(user)
            .resume(oldResume)
            .build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));
    JobDTO expectedDTO = new JobDTO(); // Build as needed
    when(jobMapper.toDTO(any(Job.class))).thenReturn(expectedDTO);

    JobDTO result = jobService.replaceResume(jobId, newResume, user);

    assertEquals(expectedDTO, result);
    verify(resumeService).deleteResume(oldResume.getId(), user);
    assertEquals(newResume, job.getResume());
    assertNull(job.getCoverLetter());
    assertNull(job.getMatchScore());
    assertNull(job.getMatchFeedback());
    assertNull(job.getMissingSkills());
    verify(jobRepository).save(job);
}
@Test
void replaceResume_shouldThrowExceptionWhenJobNotFound() {
    UUID jobId = UUID.randomUUID();
    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
    User user = new User();
    user.setUserId(UUID.randomUUID());

    assertThrows(RuntimeException.class, () -> jobService.replaceResume(jobId, new Resume(), user));
}
@Test
void replaceResume_shouldThrowExceptionWhenUnauthorized() {
    UUID jobId = UUID.randomUUID();
    User jobOwner = new User();
    jobOwner.setUserId(UUID.randomUUID());
    User attacker = new User();
    attacker.setUserId(UUID.randomUUID());

    Job job = Job.builder()
            .id(jobId)
            .user(jobOwner)
            .resume(new Resume())
            .build();

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    assertThrows(RuntimeException.class, () -> jobService.replaceResume(jobId, new Resume(), attacker));
}
@Test
void assignResume_shouldAssignNewResumeAndUpdateCoverLetter() {
    UUID jobId = UUID.randomUUID();
    UUID resumeId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    Job job = Job.builder()
            .id(jobId)
            .user(user)
            .title("Software Engineer")
            .company("Acme Corp")
            .description("Job description here")
            .build();

    Resume resume = new Resume();
    resume.setId(resumeId);
    resume.setParsedSummary("Resume Summary");

    CoverLetter coverLetter = new CoverLetter();
    coverLetter.setId(UUID.randomUUID());
    coverLetter.setContent("Old Cover Letter");
    coverLetter.setJob(job);
    job.setCoverLetter(coverLetter);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
    when(openAiService.generateCoverLetter(anyString(), anyString(), anyString(), anyString()))
            .thenReturn("New Cover Letter Content");
    when(coverLetterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(jobRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(jobMapper.toDTO(any(Job.class))).thenReturn(JobDTO.builder().id(jobId).build());

    JobDTO result = jobService.assignResume(jobId, resumeId, user);

    assertNotNull(result);
    assertEquals(jobId, result.getId());

    assertEquals(resume, job.getResume());

    assertEquals("New Cover Letter Content", coverLetter.getContent());
    assertNotNull(coverLetter.getUpdatedAt());
    assertFalse(coverLetter.isFinalVersion());

    verify(jobRepository).findById(jobId);
    verify(resumeRepository).findById(resumeId);
    verify(openAiService).generateCoverLetter(eq("Resume Summary"), eq("Software Engineer"), eq("Acme Corp"), eq("Job description here"));
    verify(coverLetterRepository).save(any(CoverLetter.class));
    verify(jobRepository).save(job);
    verify(jobMapper).toDTO(job);
}
@Test
void assignResume_shouldThrowWhenJobNotFound() {
    // Arrange
    UUID jobId = UUID.randomUUID();
    UUID resumeId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            jobService.assignResume(jobId, resumeId, user));

    assertEquals("Job not found", exception.getMessage());
    verify(jobRepository).findById(jobId);
}

@Test
void assignResume_shouldThrowWhenResumeNotFound() {
    UUID jobId = UUID.randomUUID();
    UUID resumeId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    Job job = new Job();
    job.setId(jobId);
    job.setUser(user);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            jobService.assignResume(jobId, resumeId, user));

    assertEquals("Resume  not found", exception.getMessage());
    verify(jobRepository).findById(jobId);
    verify(resumeRepository).findById(resumeId);
}
@Test
void assignResume_shouldThrowWhenUnauthorized() {
    UUID jobId = UUID.randomUUID();
    UUID resumeId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    Job job = new Job();
    job.setId(jobId);
    User differentUser = new User();
    differentUser.setUserId(UUID.randomUUID());
    job.setUser(differentUser);

    Resume resume = new Resume();
    resume.setId(resumeId);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
    when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            jobService.assignResume(jobId, resumeId, user));

    assertEquals("Unauthorized", exception.getMessage());
    verify(jobRepository).findById(jobId);
    verify(resumeRepository).findById(resumeId);
}
@Test
void deleteJobById_shouldDeleteJobSuccessfully() {
    UUID jobId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    Job job = new Job();
    job.setId(jobId);
    job.setUser(user);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    jobService.deleteJobById(jobId, user);

    verify(jobRepository).findById(jobId);
    verify(jobRepository).delete(job);
}
@Test
void deleteJobById_shouldThrowWhenJobNotFound() {
    UUID jobId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            jobService.deleteJobById(jobId, user));

    assertEquals("Job not found", exception.getMessage());
    verify(jobRepository).findById(jobId);
    verify(jobRepository, never()).delete(any());
}
@Test
void deleteJobById_shouldThrowWhenUnauthorized() {
    UUID jobId = UUID.randomUUID();
    User user = new User();
    user.setUserId(UUID.randomUUID());

    User otherUser = new User();
    otherUser.setUserId(UUID.randomUUID());

    Job job = new Job();
    job.setId(jobId);
    job.setUser(otherUser);

    when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            jobService.deleteJobById(jobId, user));

    assertEquals("Access denied: You are not authorized to delete this job.", exception.getMessage());
    verify(jobRepository).findById(jobId);
    verify(jobRepository, never()).delete(any());
}
@Test
void openAiMatchSummary_shouldCallOpenAiServiceWithCorrectArguments() {
    Job job = new Job();
    job.setDescription("Job description here");

    Resume resume = new Resume();
    resume.setParsedSummary("Resume summary here");

    when(openAiService.getMatchExplanation("Resume summary here", "Job description here"))
        .thenReturn("Match score: 75");

    String result = jobService.openAiMatchSummary(job, resume);

    assertEquals("Match score: 75", result);
    verify(openAiService).getMatchExplanation("Resume summary here", "Job description here");
}

}