package com.example.jobpilot.user.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String jobTitle;
    private String location;
}
