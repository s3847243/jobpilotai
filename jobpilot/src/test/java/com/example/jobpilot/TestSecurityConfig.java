package com.example.jobpilot;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.jobpilot.config.security.JwtAuthFilter;

@Configuration
public class TestSecurityConfig {
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return Mockito.mock(JwtAuthFilter.class);
    }
}