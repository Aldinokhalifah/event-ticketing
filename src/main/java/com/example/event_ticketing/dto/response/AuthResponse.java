package com.example.event_ticketing.dto.response;

import com.example.event_ticketing.models.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String nama;
    private String email;
    private Role role;
}