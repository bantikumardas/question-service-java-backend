package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.LoginRequest;
import com.question.service.question_service.dto.request.RegisterRequest;
import com.question.service.question_service.dto.response.AuthResponse;
import com.question.service.question_service.exception.DuplicateResourceException;
import com.question.service.question_service.exception.UnauthorizedException;
import com.question.service.question_service.models.Company;
import com.question.service.question_service.models.User;
import com.question.service.question_service.repository.CompanyRepository;
import com.question.service.question_service.repository.UserRepository;
import com.question.service.question_service.security.JwtUtil;
import com.question.service.question_service.service.AuthService;
import com.question.service.question_service.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${Admin.key}")
    private String adminKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        String key = request.getKey();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }
        //ADMIN VALIDATION
        if(request.getRole() == User.Role.ADMIN ) {
            if(key==null || key.isEmpty()) {
                throw new UnauthorizedException("Invalid key to register as ADMIN");
            }
            if(!key.equals(adminKey)) {
                throw new UnauthorizedException("Invalid key to register as ADMIN");
            }
        }
        //COMPANY ADMIN VALIDATION
        String domain=email.split("@")[1];
        Company company=companyRepository.findByEmailDomain(domain);
        if(request.getRole() == User.Role.CAADMIN ) {
            if(company==null) {
                throw new UnauthorizedException("No company registered with your email domain: " + domain);
            }
            if(key==null || key.isEmpty()) {
                throw new UnauthorizedException("Invalid key to register as COMPANY");
            }
            if(!key.equals(company.getCompanyKey())){
                throw new UnauthorizedException("Invalid key to register as COMPANY");
            }
        }
        User.Role role = (request.getRole() != null) ? request.getRole() : User.Role.CANDIDATE;

        User user = User.builder()
                .name(request.getName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .company(company)
                .build();

        user = userRepository.save(user);
        log.info("Registered userId={} role={}", user.getUserId(), user.getRole());

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());
        return buildAuthResponse(token, user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase();
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .orElseThrow(() -> {
                    log.warn("Failed login attempt for email={}", email);
                    return new UnauthorizedException("Invalid email or password");
                });

        log.info("Login userId={}", user.getUserId());
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());
        return buildAuthResponse(token, user);
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) return;
        String token = bearerToken.substring(7);
        try {
            Claims claims = jwtUtil.validateAndExtractClaims(token);
            long ttl = jwtUtil.getRemainingTtlSeconds(claims);
            tokenBlacklistService.blacklist(claims.getId(), ttl);
            log.info("Logout jti={}", claims.getId());
        } catch (Exception e) {
            log.debug("Logout with invalid token: {}", e.getMessage());
        }
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMs() / 1000)
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .logoUrl(user.getCompany() != null ? user.getCompany().getLogoUrl() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getName() : null)
                .build();
    }
}
