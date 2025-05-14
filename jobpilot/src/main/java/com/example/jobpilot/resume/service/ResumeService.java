package com.example.jobpilot.resume.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.resume.dto.ParsedResumeDTO;
import com.example.jobpilot.resume.model.MultipartInputStreamFileResource;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.repository.ResumeRepository;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final S3Service s3Service;
    private final OpenAiService openAiService;

   public Resume uploadResume(MultipartFile file, User user) throws IOException {
        String s3Url = s3Service.uploadFile(file);

        
        String resumeText = extractTextFromPdf(file); // You’ll need a PDF parser like Apache PDFBox or Tika
        ParsedResumeDTO parsed = openAiService.extractResumeInfo(resumeText);

        Resume resume = Resume.builder()
                .user(user)
                .filename(file.getOriginalFilename())
                .s3Url(s3Url)
                .parsedName(parsed.getName())
                .parsedEmail(parsed.getEmail())
                .parsedPhone(parsed.getPhone())
                .parsedSkills(parsed.getSkills())
                .parsedSummary(parsed.getSummary())
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

        if (!resume.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        resumeRepository.delete(resume);
    }


    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    public void deleteFile(Resume resume) {
        if (resume == null) return;

        try {
            // Extract file key from the S3 URL
            String s3Url = resume.getS3Url();
            String fileKey = extractS3Key(s3Url);

            // Delete file from S3
            s3Service.deleteFile(fileKey);

            // Delete resume from DB
            resumeRepository.delete(resume);

        } catch (Exception e) {
            System.err.println("Failed to delete resume: " + e.getMessage());
            // You may want to log this instead of printing
        }
    }
    private String extractS3Key(String s3Url) {
        return s3Url.substring(s3Url.lastIndexOf("/") + 1);
    }

    public Resume getResumeByIdForUser(UUID resumeId, User user) {
    Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied to this resume");
        }

        return resume;
    }

}