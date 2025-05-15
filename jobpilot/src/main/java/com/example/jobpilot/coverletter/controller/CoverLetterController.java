package com.example.jobpilot.coverletter.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.auth.service.JwtService;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.coverletter.model.CoverLetter;
import com.example.jobpilot.coverletter.service.CoverLetterService;
import com.example.jobpilot.resume.model.Resume;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cover-letters")
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;
    private final JwtService jwtService;
    private final UserRepository userRepository; // remove this

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new RuntimeException("JWT token not found in cookies");
    }
    
    private User getUserFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        String email = jwtService.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @GetMapping
    public ResponseEntity<List<CoverLetter>> getAllCoverLetters(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseEntity.ok(coverLetterService.getAllCoverLettersByUser(user));
    }
    @GetMapping("/{coverLetterId}")
    public ResponseEntity<CoverLetter> getCoverLetterById(@PathVariable UUID coverLetterId, HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseEntity.ok(coverLetterService.getCoverLetterByIdForUser(coverLetterId, user));
    }

    // ✅ 3. Generate a new cover letter using request body (CoverLetterRequest DTO)
    @PostMapping("/generate")
    public ResponseEntity<CoverLetterResponse> generateCoverLetter(
            @RequestBody CoverLetterRequest request,
            HttpServletRequest httpRequest
    ) {
        User user = getUserFromRequest(httpRequest);
        CoverLetterResponse response = coverLetterService.generateCoverLetter(request,user);
        return ResponseEntity.ok(response);
    }

    // ✅ 4. Improve an existing cover letter
    @PostMapping("/{coverLetterId}/improve")
    public ResponseEntity<String> improveCoverLetter(
            @PathVariable UUID coverLetterId,
            @RequestBody Map<String, String> payload,
            HttpServletRequest request
    ) {
        User user = getUserFromRequest(request);
        String instruction = payload.get("instruction");
        if (instruction == null || instruction.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(coverLetterService.improveCoverLetter(coverLetterId, instruction, user));
    }

    // ✅ 5. Update a cover letter manually
    @PutMapping("/{coverLetterId}")
    public ResponseEntity<CoverLetter> updateCoverLetter(
            @PathVariable UUID coverLetterId,
            @RequestBody Map<String, String> payload,
            HttpServletRequest request
    ) {
        User user = getUserFromRequest(request);
        String newText = payload.get("text");
        if (newText == null || newText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(coverLetterService.updateCoverLetter(coverLetterId, newText, user));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<CoverLetterResponse> getCoverLetterByJobId(
            @PathVariable UUID jobId,
            HttpServletRequest request
    ) {
        User user = getUserFromRequest(request);
        CoverLetter coverLetter = coverLetterService.getCoverLetterByJobId(jobId, user);
        return ResponseEntity.ok(new CoverLetterResponse(coverLetter.getContent()));
    }

}