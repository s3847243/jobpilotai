package com.example.jobpilot.resume.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final S3Service s3Service;

    public Resume uploadResume(MultipartFile file, User user) throws IOException {
        String s3Url = s3Service.uploadFile(file);

        // MOCKED parsing logic (replace with real later)
        String parsedName = "John Doe";
        String parsedEmail = "john@example.com";
        String parsedPhone = "123-456-7890";
        List<String> parsedSkills = List.of("Java", "Spring Boot", "AWS");
        String parsedSummary = "Experienced backend engineer with a focus on cloud and APIs.";

        Resume resume = Resume.builder()
                .user(user)
                .filename(file.getOriginalFilename())
                .s3Url(s3Url)
                .parsedName(parsedName)
                .parsedEmail(parsedEmail)
                .parsedPhone(parsedPhone)
                .parsedSkills(parsedSkills)
                .parsedSummary(parsedSummary)
                .uploadedAt(Instant.now())
                .build();

        return resumeRepository.save(resume);
    }

    public List<Resume> getResumesByUser(User user) {
        return resumeRepository.findByUser(user);
    }

    public void deleteResume(UUID resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        resumeRepository.delete(resume);
    }
}