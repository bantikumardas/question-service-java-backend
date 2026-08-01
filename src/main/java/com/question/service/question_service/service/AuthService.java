package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.LoginRequest;
import com.question.service.question_service.dto.request.RegisterRequest;
import com.question.service.question_service.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String bearerToken);
}
