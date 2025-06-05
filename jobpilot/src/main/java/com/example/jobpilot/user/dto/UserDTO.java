package com.example.jobpilot.user.dto;

import java.util.UUID;

import com.example.jobpilot.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String jobTitle;
    private String location;
    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .location(user.getLocation())
                .jobTitle(user.getJobTitle())
                .build();
    }
}
