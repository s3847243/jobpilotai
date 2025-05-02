package com.example.jobpilot.resume.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

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

   public Resume uploadResume(MultipartFile file, User user) throws IOException {
    String s3Url = s3Service.uploadFile(file);

    
    ParsedResumeDTO parsed = parseWithPythonParser(file); 

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

    private ParsedResumeDTO parseWithPythonParser(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ParsedResumeDTO> response = restTemplate.postForEntity(
                "http://localhost:8000/parse",
                requestEntity,
                ParsedResumeDTO.class
        );

        return response.getBody();
    }

}