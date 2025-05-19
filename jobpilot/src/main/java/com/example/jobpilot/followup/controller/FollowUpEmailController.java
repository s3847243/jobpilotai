package com.example.jobpilot.followup.controller;

import java.util.List;
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
import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.dto.ImproveEmailRequest;
import com.example.jobpilot.followup.mappers.FollowUpEmailMapper;
import com.example.jobpilot.followup.model.FollowUpEmail;
import com.example.jobpilot.followup.service.FollowUpEmailService;
import com.example.jobpilot.user.model.User;
import com.example.jobpilot.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follow-up")
@RequiredArgsConstructor
public class FollowUpEmailController {

    private final FollowUpEmailService followUpEmailService;
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
    @PostMapping("/generate/{jobId}")
    public ResponseEntity<FollowUpEmailDTO> generateFollowUpEmail(@PathVariable UUID jobId,HttpServletRequest request) {
        User user = getUserFromRequest(request);
        FollowUpEmailDTO dto = followUpEmailService.generateFollowUpEmail(jobId, user.getUserId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{followUpId}")
    public ResponseEntity<FollowUpEmailDTO> getFollowUpById(@PathVariable UUID followUpId,HttpServletRequest request) {
        User user = getUserFromRequest(request);

        FollowUpEmailDTO dto = followUpEmailService.getById(followUpId, user.getUserId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FollowUpEmailDTO>> getAllForUser(HttpServletRequest request) {
         User user = getUserFromRequest(request);

        List<FollowUpEmailDTO> dtos = followUpEmailService.getAllForUser(user.getUserId());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{followUpId}/improve")
    public ResponseEntity<FollowUpEmailDTO> improveFollowUpEmail(
            @PathVariable UUID followUpId,
            @RequestBody ImproveEmailRequest request,HttpServletRequest httpServletRequest
    ) {
         User user = getUserFromRequest(httpServletRequest);
        FollowUpEmailDTO dto = followUpEmailService.improveFollowUpEmail(followUpId, user.getUserId(), request.getInstructions());
        return ResponseEntity.ok(dto);
    }
}