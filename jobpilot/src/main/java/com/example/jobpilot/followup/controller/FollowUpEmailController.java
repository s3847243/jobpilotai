package com.example.jobpilot.followup.controller;

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
import com.example.jobpilot.followup.dto.FollowUpEmailDTO;
import com.example.jobpilot.followup.dto.ImproveEmailRequest;
import com.example.jobpilot.followup.service.FollowUpEmailService;
import com.example.jobpilot.user.model.UserPrincipal;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/follow-up")
@RequiredArgsConstructor
public class FollowUpEmailController {

    private final FollowUpEmailService followUpEmailService;
    
    @PostMapping("/generate/{jobId}")
    public ResponseEntity<FollowUpEmailDTO> generateFollowUpEmail(@PathVariable UUID jobId,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        FollowUpEmailDTO dto = followUpEmailService.generateFollowUpEmail(jobId, userPrincipal.getUser());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{followUpId}")
    public ResponseEntity<FollowUpEmailDTO> getFollowUpById(@PathVariable UUID followUpId,@AuthenticationPrincipal UserPrincipal userPrincipal) {

        FollowUpEmailDTO dto = followUpEmailService.getById(followUpId, userPrincipal.getUser().getUserId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FollowUpEmailDTO>> getAllForUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<FollowUpEmailDTO> dtos = followUpEmailService.getAllForUser(userPrincipal.getUser().getUserId());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{followUpId}/improve")
    public ResponseEntity<FollowUpEmailDTO> improveFollowUpEmail(
            @PathVariable UUID followUpId,
            @RequestBody ImproveEmailRequest request,@AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        FollowUpEmailDTO dto = followUpEmailService.improveFollowUpEmail(followUpId, userPrincipal.getUser().getUserId(), request.getInstructions());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{followUpEmailId}")
    public ResponseEntity<?> deleteFollowUpEmail(
            @PathVariable UUID followUpEmailId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        followUpEmailService.deleteFollowUpEmail(followUpEmailId, userPrincipal.getUser());
        return ResponseEntity.ok("Follow-up email deleted successfully.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }
}