package com.example.jobpilot.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String jobTitle;
    private String location;
    private String linkedinUrl;

}
