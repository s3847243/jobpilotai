package com.example.jobpilot.user.service;

import java.time.Instant;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jobpilot.user.repository.UserRepository;
import com.example.jobpilot.user.dto.UpdateUserRequest;
import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return mapToDto(user);
    }

    public UserDTO updateUser(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getJobTitle() != null) {
            user.setJobTitle(request.getJobTitle());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return mapToDto(user);
    }

    private UserDTO mapToDto(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .jobTitle(user.getJobTitle())
                .location(user.getLocation())
                .build();
    }
}