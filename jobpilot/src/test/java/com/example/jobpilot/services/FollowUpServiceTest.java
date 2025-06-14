package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.mappers.FollowUpEmailMapper;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.followup.repository.FollowUpEmailRepository;
import com.example.jobpilot.followup.service.FollowUpEmailService;
import com.example.jobpilot.job.model.Job;
import com.example.jobpilot.job.repository.JobRepository;
import com.example.jobpilot.user.model.User;
import org.junit.jupiter.api.extension.ExtendWith;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowUpEmailServiceTest {

    @Mock
    private FollowUpEmailRepository followUpEmailRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private OpenAiService openAiService;

    @Mock
    private FollowUpEmailMapper followUpEmailMapper;

    @InjectMocks
    private FollowUpEmailService followUpEmailService;

    private Job job;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");

        job = new Job();
        job.setId(UUID.randomUUID());
        job.setUser(user);
        job.setTitle("Backend Developer");
        job.setCompany("TechCorp");
        job.setDescription("Develop and maintain backend services.");
    }

    @Test
    void generateFollowUpEmail_success() {
        String generatedContent = "This is a follow-up email.";
        FollowUpEmail savedEmail = new FollowUpEmail();
        savedEmail.setId(UUID.randomUUID());
        savedEmail.setBody(generatedContent);
        savedEmail.setJob(job);
        savedEmail.setUser(user);
        savedEmail.setCreatedAt(Instant.now());

        FollowUpEmailDTO emailDTO = new FollowUpEmailDTO();
        emailDTO.setId(savedEmail.getId());
        emailDTO.setBody(savedEmail.getBody());

        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        when(openAiService.generateFollowUpEmailPrompt(any(Job.class))).thenReturn(generatedContent);
        when(followUpEmailRepository.save(any(FollowUpEmail.class))).thenReturn(savedEmail);
        when(followUpEmailMapper.toDTO(any(FollowUpEmail.class))).thenReturn(emailDTO);

        
        FollowUpEmailDTO result = followUpEmailService.generateFollowUpEmail(job.getId(), user);

        assertNotNull(result);
        assertEquals(savedEmail.getId(), result.getId());
        assertEquals(generatedContent, result.getBody());

        verify(jobRepository).findById(job.getId());
        verify(openAiService).generateFollowUpEmailPrompt(any(Job.class));
        verify(followUpEmailRepository).save(any(FollowUpEmail.class));
        verify(jobRepository).save(job); // optional if cascade is used
        verify(followUpEmailMapper).toDTO(any(FollowUpEmail.class));
    }

    @Test
    void generateFollowUpEmail_alreadyExists_shouldThrow() {
        FollowUpEmail existingEmail = new FollowUpEmail();
        job.setFollowUpEmail(existingEmail);

        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followUpEmailService.generateFollowUpEmail(job.getId(), user));

        assertEquals("Follow-up email already exists for this job", ex.getMessage());
        verify(jobRepository).findById(job.getId());
        verifyNoMoreInteractions(followUpEmailRepository, openAiService, followUpEmailMapper);
    }

    @Test
    void generateFollowUpEmail_jobNotFound_shouldThrow() {
        UUID randomJobId = UUID.randomUUID();
        when(jobRepository.findById(randomJobId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followUpEmailService.generateFollowUpEmail(randomJobId, user));

        assertEquals("Job not found or unauthorized", ex.getMessage());
        verify(jobRepository).findById(randomJobId);
        verifyNoMoreInteractions(followUpEmailRepository, openAiService, followUpEmailMapper);
    }

    @Test
    void getById_shouldReturnFollowUpEmailDTO_whenExists() {
        UUID followUpId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        FollowUpEmail email = new FollowUpEmail();
        email.setId(followUpId);
        email.setUser(new User());
        email.getUser().setUserId(userId);
        email.setBody("Test email body");

        FollowUpEmailDTO emailDTO = new FollowUpEmailDTO();
        emailDTO.setId(followUpId);
        emailDTO.setBody("Test email body");

        when(followUpEmailRepository.findByIdAndUserId(followUpId, userId)).thenReturn(Optional.of(email));
        when(followUpEmailMapper.toDTO(email)).thenReturn(emailDTO);

        
        FollowUpEmailDTO result = followUpEmailService.getById(followUpId, userId);

        assertNotNull(result);
        assertEquals(followUpId, result.getId());
        assertEquals("Test email body", result.getBody());
        verify(followUpEmailRepository).findByIdAndUserId(followUpId, userId);
        verify(followUpEmailMapper).toDTO(email);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        UUID followUpId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(followUpEmailRepository.findByIdAndUserId(followUpId, userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followUpEmailService.getById(followUpId, userId));

        assertEquals("Follow-up not found or unauthorized", ex.getMessage());
        verify(followUpEmailRepository).findByIdAndUserId(followUpId, userId);
        verifyNoMoreInteractions(followUpEmailMapper);
    }


    @Test
    void getAllForUser_shouldReturnListOfDTOs() {
        UUID userId = UUID.randomUUID();

        FollowUpEmail email1 = new FollowUpEmail();
        email1.setId(UUID.randomUUID());
        email1.setBody("Email 1 body");

        FollowUpEmail email2 = new FollowUpEmail();
        email2.setId(UUID.randomUUID());
        email2.setBody("Email 2 body");

        List<FollowUpEmail> emails = List.of(email1, email2);

        FollowUpEmailDTO dto1 = new FollowUpEmailDTO();
        dto1.setId(email1.getId());
        dto1.setBody("Email 1 body");

        FollowUpEmailDTO dto2 = new FollowUpEmailDTO();
        dto2.setId(email2.getId());
        dto2.setBody("Email 2 body");

        when(followUpEmailRepository.findAllByUserId(userId)).thenReturn(emails);
        when(followUpEmailMapper.toDTO(email1)).thenReturn(dto1);
        when(followUpEmailMapper.toDTO(email2)).thenReturn(dto2);

        List<FollowUpEmailDTO> result = followUpEmailService.getAllForUser(userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(email1.getId())));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(email2.getId())));

        verify(followUpEmailRepository).findAllByUserId(userId);
        verify(followUpEmailMapper).toDTO(email1);
        verify(followUpEmailMapper).toDTO(email2);
    }
    @Test
    void improveFollowUpEmail_shouldUpdateBodyAndReturnDTO() {
        UUID followUpId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String instruction = "Make it more concise.";

        FollowUpEmail existingEmail = new FollowUpEmail();
        existingEmail.setId(followUpId);
        existingEmail.setBody("Old follow-up email.");
        existingEmail.setUser(User.builder().userId(userId).build());

        String improvedText = "Improved follow-up email.";

        FollowUpEmailDTO dto = new FollowUpEmailDTO();
        dto.setId(followUpId);
        dto.setBody(improvedText);

        when(followUpEmailRepository.findByIdAndUserId(followUpId, userId)).thenReturn(Optional.of(existingEmail));
        when(openAiService.buildImprovementPrompt(any(FollowUpEmail.class), eq(instruction))).thenReturn(improvedText);
        when(followUpEmailRepository.save(existingEmail)).thenReturn(existingEmail);
        when(followUpEmailMapper.toDTO(existingEmail)).thenReturn(dto);

        FollowUpEmailDTO result = followUpEmailService.improveFollowUpEmail(followUpId, userId, instruction);

        assertEquals(improvedText, result.getBody());
        verify(followUpEmailRepository).findByIdAndUserId(followUpId, userId);
        verify(openAiService).buildImprovementPrompt(existingEmail, instruction);
        verify(followUpEmailRepository).save(existingEmail);
        verify(followUpEmailMapper).toDTO(existingEmail);
    }

    @Test
    void improveFollowUpEmail_shouldThrowExceptionForEmptyInstructions() {
        UUID followUpId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        FollowUpEmail email = new FollowUpEmail();
        email.setId(followUpId);
        email.setBody("Old body");
        email.setUser(User.builder().userId(userId).build());

        when(followUpEmailRepository.findByIdAndUserId(followUpId, userId)).thenReturn(Optional.of(email));

        assertThrows(IllegalArgumentException.class, () ->
            followUpEmailService.improveFollowUpEmail(followUpId, userId, "   ")
        );

        verifyNoInteractions(openAiService);
    }

    @Test
    void deleteFollowUpEmail_shouldDeleteWhenAuthorized() {
        UUID followUpId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().userId(userId).build();

        FollowUpEmail followUpEmail = new FollowUpEmail();
        followUpEmail.setId(followUpId);
        followUpEmail.setUser(user);

        when(followUpEmailRepository.findById(followUpId)).thenReturn(Optional.of(followUpEmail));

        followUpEmailService.deleteFollowUpEmail(followUpId, user);

        verify(followUpEmailRepository).findById(followUpId);
        verify(followUpEmailRepository).delete(followUpEmail);
    }

    @Test
    void deleteFollowUpEmail_shouldThrowExceptionWhenUnauthorized() {
        UUID followUpId = UUID.randomUUID();
        User owner = User.builder().userId(UUID.randomUUID()).build();
        User otherUser = User.builder().userId(UUID.randomUUID()).build();

        FollowUpEmail followUpEmail = new FollowUpEmail();
        followUpEmail.setId(followUpId);
        followUpEmail.setUser(owner);

        when(followUpEmailRepository.findById(followUpId)).thenReturn(Optional.of(followUpEmail));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            followUpEmailService.deleteFollowUpEmail(followUpId, otherUser)
        );

        assertEquals("Access denied: You are not authorized to delete this follow-up email.", exception.getMessage());
        verify(followUpEmailRepository).findById(followUpId);
        verify(followUpEmailRepository, never()).delete(any());
    }

    @Test
    void deleteFollowUpEmail_shouldThrowExceptionWhenNotFound() {
        UUID followUpId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();

        when(followUpEmailRepository.findById(followUpId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            followUpEmailService.deleteFollowUpEmail(followUpId, user)
        );

        assertEquals("Follow-up email not found", exception.getMessage());
        verify(followUpEmailRepository).findById(followUpId);
        verify(followUpEmailRepository, never()).delete(any());
    }




}