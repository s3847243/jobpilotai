package com.example.jobpilot.user.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String name;
    private String location;
    private String jobTitle;
    private String phone;
}