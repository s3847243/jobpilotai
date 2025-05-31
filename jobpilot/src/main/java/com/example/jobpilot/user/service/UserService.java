package com.example.jobpilot.user.service;

import java.time.Instant;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jobpilot.user.repository.UserRepository;
import com.example.jobpilot.user.dto.UserDTO;
import com.example.jobpilot.user.dto.UserProfileUpdateRequest;
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

    public UserDTO updateProfile(User user, UserProfileUpdateRequest request) {
        if (request.getName() != null) user.setFullName(request.getName());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getJobTitle() != null) user.setJobTitle(request.getJobTitle());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        userRepository.save(user);
        return UserDTO.from(user);
    }

    public void deleteAccount(User user) {
        userRepository.delete(user);
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