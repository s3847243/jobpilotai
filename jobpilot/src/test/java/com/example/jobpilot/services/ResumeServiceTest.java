package com.example.jobpilot.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.job.mapper.JobSummaryMapper;
import com.example.jobpilot.resume.dto.ParsedResumeDTO;
import com.example.jobpilot.resume.dto.ResumeDTO;
import com.example.jobpilot.resume.mapper.ResumeMapper;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.resume.service.S3Service;
import com.example.jobpilot.user.model.User;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private S3Service s3Service;
    @Mock private OpenAiService openAiService;
    @Mock private ResumeMapper resumeMapper;
    @Mock private JobSummaryMapper jobMapper;
    
    @InjectMocks private ResumeService resumeService;

    @Test
    void uploadResume_shouldUploadFileProcessAndReturnDTO() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        User user = User.builder().userId(UUID.randomUUID()).build();

        // pdf bytes generated
        byte[] pdfBytes = (
            "%PDF-1.4\n" +
            "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n" +
            "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n" +
            "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] >>\nendobj\n" +
            "xref\n0 4\n0000000000 65535 f \n" +
            "0000000010 00000 n \n0000000053 00000 n \n0000000106 00000 n \n" +
            "trailer\n<< /Root 1 0 R /Size 4 >>\nstartxref\n161\n%%EOF"
        ).getBytes(StandardCharsets.US_ASCII);

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(pdfBytes));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);

        when(file.getOriginalFilename()).thenReturn("resume.pdf");
        when(file.getInputStream()).thenReturn(inputStream); // ✅ Mock this

        when(s3Service.uploadFile(file)).thenReturn("https://s3-bucket/resume.pdf");

        ParsedResumeDTO parsedResume = new ParsedResumeDTO();
        parsedResume.setName("John Doe");
        parsedResume.setEmail("john@example.com");
        parsedResume.setPhone("123456789");
        parsedResume.setSkills(List.of("Java", "Spring"));
        parsedResume.setSummary("Experienced developer");
        parsedResume.setAtsScore(85.0);
        when(openAiService.extractResumeInfo(anyString())).thenReturn(parsedResume);

        Resume savedResume = Resume.builder()
                .id(UUID.randomUUID())
                .user(user)
                .filename("resume.pdf")
                .s3Url("https://s3-bucket/resume.pdf")
                .parsedName("John Doe")
                .parsedEmail("john@example.com")
                .parsedPhone("123456789")
                .parsedSkills(List.of("Java", "Spring"))
                .parsedSummary("Experienced developer")
                .atsScore(85.0)
                .uploadedAt(Instant.now())
                .build();
        when(resumeRepository.save(any())).thenReturn(savedResume);

        ResumeDTO resumeDTO = new ResumeDTO();
        when(resumeMapper.toDTO(savedResume)).thenReturn(resumeDTO);

        // Act
        ResumeDTO result = resumeService.uploadResume(file, user);

        // Assert
        assertNotNull(result);
        verify(s3Service).uploadFile(file);
        verify(openAiService).extractResumeInfo(anyString());
        verify(resumeRepository).save(any());
        verify(resumeMapper).toDTO(savedResume);
    }
    @Test
    void getResumesByUser_shouldReturnMappedResumes() {
        User user = User.builder().userId(UUID.randomUUID()).build();

        Resume resume1 = Resume.builder().id(UUID.randomUUID()).user(user).filename("resume1.pdf").build();
        Resume resume2 = Resume.builder().id(UUID.randomUUID()).user(user).filename("resume2.pdf").build();
        List<Resume> resumeList = List.of(resume1, resume2);

        when(resumeRepository.findByUser(user)).thenReturn(resumeList);

        ResumeDTO resumeDTO1 = new ResumeDTO();
        ResumeDTO resumeDTO2 = new ResumeDTO();
        when(resumeMapper.toDTO(resume1)).thenReturn(resumeDTO1);
        when(resumeMapper.toDTO(resume2)).thenReturn(resumeDTO2);

        List<ResumeDTO> result = resumeService.getResumesByUser(user);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(resumeDTO1));
        assertTrue(result.contains(resumeDTO2));

        verify(resumeRepository).findByUser(user);
        verify(resumeMapper).toDTO(resume1);
        verify(resumeMapper).toDTO(resume2);
    }
    @Test
    void deleteResume_shouldDeleteResumeAndFile_whenUserIsOwner() {
        UUID resumeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).build();

        Resume resume = Resume.builder()
                .id(resumeId)
                .user(user)
                .s3Url("https://my-bucket.s3.amazonaws.com/file.pdf")
                .build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        resumeService.deleteResume(resumeId, user);

        verify(resumeRepository).findById(resumeId);
        verify(s3Service).deleteFile("file.pdf");
        verify(resumeRepository).delete(resume);
    }
    @Test
    void deleteResume_shouldThrowException_whenUserNotOwner() {
        UUID resumeId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();
        User otherUser = User.builder().userId(UUID.randomUUID()).build();

        Resume resume = Resume.builder()
                .id(resumeId)
                .user(otherUser)
                .build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        assertThrows(RuntimeException.class, () -> resumeService.deleteResume(resumeId, user));

        verify(resumeRepository).findById(resumeId);
        verify(s3Service, never()).deleteFile(any());
        verify(resumeRepository, never()).delete(any());
    }
    @Test
    void deleteResume_shouldThrowException_whenResumeNotFound() {
        UUID resumeId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resumeService.deleteResume(resumeId, user));

        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(s3Service);
        verify(resumeRepository, never()).delete(any());
    }
    @Test
    void getResumeByIdForUser_shouldReturnResumeDTO_whenUserIsOwner() {
        UUID resumeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).build();

        Resume resume = Resume.builder()
                .id(resumeId)
                .user(user)
                .build();

        ResumeDTO expectedDTO = ResumeDTO.builder().id(resumeId).build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
        when(resumeMapper.toDTO(resume)).thenReturn(expectedDTO);

        ResumeDTO result = resumeService.getResumeByIdForUser(resumeId, user);

        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(resumeRepository).findById(resumeId);
        verify(resumeMapper).toDTO(resume);
    }
    @Test
    void getResumeByIdForUser_shouldThrowException_whenUserNotOwner() {
        UUID resumeId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();
        User otherUser = User.builder().userId(UUID.randomUUID()).build();

        Resume resume = Resume.builder()
                .id(resumeId)
                .user(otherUser)
                .build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        assertThrows(RuntimeException.class, () -> resumeService.getResumeByIdForUser(resumeId, user));

        verify(resumeRepository).findById(resumeId);
        verify(resumeMapper, never()).toDTO(any());
    }
    @Test
    void getResumeByIdForUser_shouldThrowException_whenResumeNotFound() {
        UUID resumeId = UUID.randomUUID();
        User user = User.builder().userId(UUID.randomUUID()).build();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resumeService.getResumeByIdForUser(resumeId, user));

        verify(resumeRepository).findById(resumeId);
        verifyNoInteractions(resumeMapper);
    }
    
   


}
