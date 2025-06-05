package com.example.jobpilot.coverletter.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.coverletter.dto.CoverLetterDTO;
import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.coverletter.service.CoverLetterService;
import com.example.jobpilot.user.model.UserPrincipal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cover-letters")
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @GetMapping
    public ResponseEntity<List<CoverLetterDTO>> getAllCoverLetters(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(coverLetterService.getAllCoverLettersByUser(userPrincipal.getUser()));
    }
    @GetMapping("/{coverLetterId}")
    public ResponseEntity<CoverLetterDTO> getCoverLetterById(@PathVariable UUID coverLetterId,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(coverLetterService.getCoverLetterByIdForUser(coverLetterId, userPrincipal.getUser()));
    }

    @PostMapping("/generate")
    public ResponseEntity<CoverLetterDTO> generateCoverLetter(
            @RequestBody CoverLetterRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CoverLetterDTO response = coverLetterService.generateCoverLetter(request,userPrincipal.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{coverLetterId}/improve")
    public ResponseEntity<String> improveCoverLetter(
            @PathVariable UUID coverLetterId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String instruction = payload.get("instruction");
        if (instruction == null || instruction.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(coverLetterService.improveCoverLetter(coverLetterId, instruction, userPrincipal.getUser()));
    }

    @PutMapping("/{coverLetterId}")
    public ResponseEntity<CoverLetterDTO> updateCoverLetter(
            @PathVariable UUID coverLetterId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String newText = payload.get("text");
        if (newText == null || newText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(coverLetterService.updateCoverLetter(coverLetterId, newText, userPrincipal.getUser()));
    }

    @DeleteMapping("/{coverLetterId}")
    public ResponseEntity<?> deleteCoverLetter(
            @PathVariable UUID coverLetterId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        coverLetterService.deleteCoverLetter(coverLetterId, userPrincipal.getUser());
        return ResponseEntity.ok("Cover letter deleted successfully.");
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }


}