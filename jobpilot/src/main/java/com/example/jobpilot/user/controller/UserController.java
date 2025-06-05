package com.example.jobpilot.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.dto.UserProfileUpdateRequest;
import com.example.jobpilot.user.model.UserPrincipal;
import com.example.jobpilot.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @GetMapping("/me")
    // public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
    //     String email = authentication.getName();
    //     UserDTO user = userService.getUserByEmail(email);
    //     return ResponseEntity.ok(user);
    // }

    @PatchMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserProfileUpdateRequest request
    ) {
        UserDTO updatedUser = userService.updateProfile(userPrincipal.getUser(), request);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || userPrincipal.getUser() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userService.deleteAccount(userPrincipal.getUser());
        return ResponseEntity.noContent().build();
    }
}
