package com.example.jobpilot.coverletter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.coverletter.dto.CoverLetterRequest;
import com.example.jobpilot.coverletter.dto.CoverLetterResponse;
import com.example.jobpilot.coverletter.service.CoverLetterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cover-letter")
@RequiredArgsConstructor
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @PostMapping("/generate")
    public ResponseEntity<CoverLetterResponse> generate(@RequestBody CoverLetterRequest request) {
        return ResponseEntity.ok(coverLetterService.generateCoverLetter(request));
    }
}