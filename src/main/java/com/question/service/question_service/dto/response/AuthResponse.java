package com.question.service.question_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthResponse {
    private String token;
    private String tokenType;
    private long expiresIn;
    private UUID userId;
    private String email;
    private String name;
    private String role;
    private String companyName;
    private String logoUrl;
}
