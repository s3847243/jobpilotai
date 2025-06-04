package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.coverletter.dto.CoverLetterDTO;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.coverletter.mapper.CoverLetterMapper;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.repository.CoverLetterRepository;
import com.example.jobpilot.coverletter.service.CoverLetterService;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.user.model.User;

@ExtendWith(MockitoExtension.class)
class CoverLetterServiceTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private JobRepository jobRepository;
    @Mock private OpenAiService openAiService;
    @Mock private CoverLetterRepository coverLetterRepository;
    @Mock private CoverLetterMapper coverLetterMapper;

    @InjectMocks private CoverLetterService coverLetterService;

    private User user;
    private Job job;
    private Resume resume;
    private CoverLetter coverLetter;

    @BeforeEach
    void setup() {
        user = User.builder().userId(UUID.randomUUID()).build();
        job = Job.builder().id(UUID.randomUUID()).user(user).title("Dev").company("Company").description("Job desc").build();
        resume = Resume.builder().id(UUID.randomUUID()).parsedSummary("Summary").build();
        coverLetter = new CoverLetter();
        coverLetter.setId(UUID.randomUUID());
        coverLetter.setUser(user);
        coverLetter.setJob(job);
        coverLetter.setContent("Letter content");
    }

    @Test
    void generateCoverLetter_shouldCreateCoverLetter() {
        // Arrange
        UUID resumeId = resume.getId();
        UUID jobId = job.getId();
        CoverLetterRequest request = new CoverLetterRequest(resumeId, jobId);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(openAiService.generateCoverLetter(any(), any(), any(), any())).thenReturn("Generated Cover Letter Content");
        when(coverLetterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(coverLetterMapper.toDTO(any())).thenReturn(new CoverLetterDTO());

        
        CoverLetterDTO result = coverLetterService.generateCoverLetter(request, user);

        assertNotNull(result);
        verify(resumeRepository).findById(resumeId);
        verify(jobRepository).findById(jobId);
        verify(openAiService).generateCoverLetter(any(), any(), any(), any());
        verify(coverLetterRepository).save(any(CoverLetter.class));
        verify(coverLetterMapper).toDTO(any());
    }
    @Test
    void generateCoverLetter_shouldThrowIfResumeNotFound() {
        UUID resumeId = UUID.randomUUID();
        UUID jobId = job.getId();
        CoverLetterRequest request = new CoverLetterRequest(resumeId, jobId);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            coverLetterService.generateCoverLetter(request, user)
        );

        assertEquals("Resume not found", ex.getMessage());
        verify(resumeRepository).findById(resumeId);
        verifyNoMoreInteractions(jobRepository, openAiService, coverLetterRepository);
    }
    @Test
    void generateCoverLetter_shouldThrowIfJobNotFound() {
        UUID resumeId = resume.getId();
        UUID jobId = UUID.randomUUID();
        CoverLetterRequest request = new CoverLetterRequest(resumeId, jobId);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            coverLetterService.generateCoverLetter(request, user)
        );

        assertEquals("Job not found", ex.getMessage());
        verify(resumeRepository).findById(resumeId);
        verify(jobRepository).findById(jobId);
        verifyNoMoreInteractions(openAiService, coverLetterRepository);
    }

    @Test
    void getAllCoverLettersByUser_shouldReturnListOfDTOs() {
   
        List<CoverLetter> letters = List.of(
            new CoverLetter(), new CoverLetter()
        );
        when(coverLetterRepository.findAllByJobUser(user)).thenReturn(letters);
        when(coverLetterMapper.toDTO(any())).thenReturn(new CoverLetterDTO());

        List<CoverLetterDTO> result = coverLetterService.getAllCoverLettersByUser(user);

        assertNotNull(result);
        assertEquals(letters.size(), result.size());
        verify(coverLetterRepository).findAllByJobUser(user);
        verify(coverLetterMapper, times(letters.size())).toDTO(any());
    }

    @Test
    void getCoverLetter_shouldReturnContentIfAuthorized() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);
        
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setContent("Sample Cover Letter");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.of(coverLetter));

        CoverLetterResponse response = coverLetterService.getCoverLetter(jobId, user);

        assertNotNull(response);
        assertEquals("Sample Cover Letter", response.getCoverLetterText());
        verify(jobRepository).findById(jobId);
        verify(coverLetterRepository).findByJob(job);
    }
    @Test
    void getCoverLetter_shouldThrowIfUnauthorized() {
        UUID jobId = UUID.randomUUID();
        UUID jobOwnerId = UUID.randomUUID();  
        UUID otherUserId = UUID.randomUUID(); 

        User jobOwner = new User();
        jobOwner.setUserId(jobOwnerId);

        Job job = new Job();
        job.setId(jobId);
        job.setUser(jobOwner); 

        User otherUser = new User();
        otherUser.setUserId(otherUserId);  

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coverLetterService.getCoverLetter(jobId, otherUser);
        });

        assertEquals("Unauthorized access to cover letter", exception.getMessage());
    }
    @Test
    void getCoverLetter_shouldThrowIfJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.getCoverLetter(jobId, user);
        });
    }
    @Test
    void getCoverLetter_shouldThrowIfCoverLetterNotFound() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.getCoverLetter(jobId, user);
        });
    }


    @Test
    void updateCoverLetter_shouldUpdateContentIfAuthorized() {
        UUID jobId = UUID.randomUUID();
        String newContent = "Updated content";
        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);

        CoverLetter existingLetter = new CoverLetter();
        existingLetter.setContent("Old content");
        existingLetter.setJob(job);
        existingLetter.setUser(user);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.of(existingLetter));
        when(coverLetterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(coverLetterMapper.toDTO(any())).thenReturn(new CoverLetterDTO());

        CoverLetterDTO result = coverLetterService.updateCoverLetter(jobId, newContent, user);

        assertNotNull(result);
        assertEquals(newContent, existingLetter.getContent());
        verify(coverLetterRepository).save(existingLetter);
    }
    @Test
    void updateCoverLetter_shouldThrowIfUnauthorized() {
        UUID jobId = UUID.randomUUID();
        UUID jobOwnerId = UUID.randomUUID();
        UUID testUserId = UUID.randomUUID();

        User jobOwner = new User();
        jobOwner.setUserId(jobOwnerId); 

        Job job = new Job();
        job.setId(jobId);
        job.setUser(jobOwner);

        User testUser = new User();
        testUser.setUserId(testUserId); 

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coverLetterService.updateCoverLetter(jobId, "content", testUser);
        });

        assertEquals("Unauthorized", exception.getMessage());
    }
    @Test
    void updateCoverLetter_shouldThrowIfJobNotFound() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.updateCoverLetter(jobId, "content", user);
        });
    }
    @Test
    void updateCoverLetter_shouldThrowIfCoverLetterNotFound() {
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.updateCoverLetter(jobId, "content", user);
        });
    }

    @Test
    void improveCoverLetter_shouldUpdateContentIfAuthorized() {
        UUID jobId = UUID.randomUUID();
        String instruction = "Make it more professional";
        String originalContent = "Original content";
        String improvedContent = "Improved content";

        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setJob(job);
        coverLetter.setUser(user);
        coverLetter.setContent(originalContent);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.of(coverLetter));
        when(openAiService.improveText(originalContent, instruction)).thenReturn(improvedContent);
        when(coverLetterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        
        String result = coverLetterService.improveCoverLetter(jobId, instruction, user);

        assertEquals(improvedContent, result);
        assertEquals(improvedContent, coverLetter.getContent());
        verify(coverLetterRepository).save(coverLetter);
    }
    @Test
    void improveCoverLetter_shouldThrowIfUnauthorized() {
        UUID jobId = UUID.randomUUID();
        UUID jobOwnerId = UUID.randomUUID();
        UUID testUserId = UUID.randomUUID();

        User jobOwner = new User();
        jobOwner.setUserId(jobOwnerId); 

        Job job = new Job();
        job.setId(jobId);
        job.setUser(jobOwner);

        User testUser = new User();
        testUser.setUserId(testUserId);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coverLetterService.improveCoverLetter(jobId, "instruction", testUser);
        });

        assertEquals("Unauthorized", exception.getMessage());
    }
    @Test
    void improveCoverLetter_shouldThrowIfJobNotFound() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.improveCoverLetter(jobId, "instruction", user);
        });
    }
    @Test
    void improveCoverLetter_shouldThrowIfCoverLetterNotFound() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        Job job = new Job();
        job.setId(jobId);
        job.setUser(user);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(coverLetterRepository.findByJob(job)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coverLetterService.improveCoverLetter(jobId, "instruction", user);
        });
    }

    @Test
    void getCoverLetterByIdForUser_shouldReturnDTOIfAuthorized() {
        UUID coverLetterId = UUID.randomUUID();
        User user = new User();
        user.setUserId(UUID.randomUUID());

        Job job = new Job();
        job.setUser(user);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(coverLetterId);
        coverLetter.setJob(job);

        CoverLetterDTO dto = new CoverLetterDTO();
        dto.setId(coverLetterId);

        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.of(coverLetter));
        when(coverLetterMapper.toDTO(coverLetter)).thenReturn(dto);

        CoverLetterDTO result = coverLetterService.getCoverLetterByIdForUser(coverLetterId, user);

        assertEquals(dto, result);
    }

    @Test
    void getCoverLetterByIdForUser_shouldThrowIfUnauthorized() {
        UUID coverLetterId = UUID.randomUUID();
        User user = new User();
        user.setUserId(UUID.randomUUID());

        User anotherUser = new User();
        anotherUser.setUserId(UUID.randomUUID());

        Job job = new Job();
        job.setUser(anotherUser);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(coverLetterId);
        coverLetter.setJob(job);

        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.of(coverLetter));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            coverLetterService.getCoverLetterByIdForUser(coverLetterId, user)
        );

        assertEquals("Unauthorized access to this cover letter", ex.getMessage());
    }

    @Test
    void getCoverLetterByIdForUser_shouldThrowIfNotFound() {
        UUID coverLetterId = UUID.randomUUID();
        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            coverLetterService.getCoverLetterByIdForUser(coverLetterId, new User())
        );
    }

    @Test
    void deleteCoverLetter_shouldDeleteIfAuthorized() {
        UUID coverLetterId = UUID.randomUUID();
        User user = new User();
        user.setUserId(UUID.randomUUID());

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(coverLetterId);
        coverLetter.setUser(user);

        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.of(coverLetter));

        coverLetterService.deleteCoverLetter(coverLetterId, user);

        verify(coverLetterRepository).delete(coverLetter);
    }

    @Test
    void deleteCoverLetter_shouldThrowIfUnauthorized() {
        UUID coverLetterId = UUID.randomUUID();
        User user = new User();
        user.setUserId(UUID.randomUUID());

        User anotherUser = new User();
        anotherUser.setUserId(UUID.randomUUID());

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(coverLetterId);
        coverLetter.setUser(anotherUser);

        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.of(coverLetter));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            coverLetterService.deleteCoverLetter(coverLetterId, user)
        );

        assertEquals("Access denied: You are not authorized to delete this cover letter.", ex.getMessage());
    }

    @Test
    void deleteCoverLetter_shouldThrowIfNotFound() {
        UUID coverLetterId = UUID.randomUUID();
        User user = new User();

        when(coverLetterRepository.findById(coverLetterId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            coverLetterService.deleteCoverLetter(coverLetterId, user)
        );

        assertEquals("Cover Letter not found", ex.getMessage());
    }








    
}
