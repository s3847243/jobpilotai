package com.example.jobpilot.resume.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.jobpilot.ai.service.OpenAiService;
import com.example.jobpilot.resume.dto.JobSummaryDTO;
import com.example.jobpilot.resume.dto.ParsedResumeDTO;
import com.example.jobpilot.resume.dto.ResumeDTO;
import com.example.jobpilot.resume.mapper.JobSummaryMapper;
import com.example.jobpilot.resume.mapper.ResumeMapper;
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
    private final ResumeMapper resumeMapper;
    private final JobSummaryMapper jobMapper;
    public Resume internalUploadResume(MultipartFile file, User user) throws IOException {
        String s3Url = s3Service.uploadFile(file);
        String resumeText = extractTextFromPdf(file);
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
                .atsScore(parsed.getAtsScore()) 
                .uploadedAt(Instant.now())
                .build();

        return resumeRepository.save(resume);
    }
    public ResumeDTO uploadResume(MultipartFile file, User user) throws IOException {
        
            Resume resume = internalUploadResume(file, user);
        return resumeMapper.toDTO(resume);
        }

    public List<ResumeDTO> getResumesByUser(User user) {
        return resumeRepository.findByUser(user).stream()
                .map(resumeMapper::toDTO)
                .toList();
    }

    public void deleteResume(UUID resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied");
        }

        if (resume.getS3Url() != null) {
            String fileKey = extractS3Key(resume.getS3Url());
            s3Service.deleteFile(fileKey);
        }

        resumeRepository.delete(resume);
    }

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    private String extractS3Key(String s3Url) {
        return s3Url.substring(s3Url.lastIndexOf("/") + 1);
    }

    public ResumeDTO getResumeByIdForUser(UUID resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Access denied to this resume");
        }

        return resumeMapper.toDTO(resume);
    }

    // helper method that returns the entity
    public Resume getResumeEntityByIdForUser(UUID resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        return resume;
    }
    public List<JobSummaryDTO> getJobsForResume(UUID resumeId, User user) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to access this resume's jobs");
        }

        return resume.getJobs().stream()
                .map(jobMapper::toSummaryDTO)
                .toList();
    }

}