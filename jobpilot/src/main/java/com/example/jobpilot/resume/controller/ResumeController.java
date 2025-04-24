package com.example.jobpilot.resume.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.jobpilot.auth.repository.UserRepository;
import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.resume.service.ResumeService;
import com.example.jobpilot.user.model.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private User getUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/upload")
    public ResponseEntity<Resume> uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        User user = getUserFromRequest(request);
        Resume resume = resumeService.uploadResume(file, user);
        return ResponseEntity.ok(resume);
    }

    @GetMapping
    public ResponseEntity<List<Resume>> listResumes(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseEntity.ok(resumeService.getResumesByUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable UUID id, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        resumeService.deleteResume(id, user);
        return ResponseEntity.ok("Resume deleted successfully");
    }
}
