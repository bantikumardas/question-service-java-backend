package com.question.service.question_service.service;

public interface TokenBlacklistService {
    void blacklist(String jti, long ttlSeconds);
    boolean isBlacklisted(String jti);
}
