package com.question.service.question_service.service.impl;

import com.question.service.question_service.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String KEY_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void blacklist(String jti, long ttlSeconds) {
        if (ttlSeconds <= 0) return;
        try {
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", ttlSeconds, TimeUnit.SECONDS);
            log.debug("Blacklisted token jti={} ttl={}s", jti, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to blacklist token jti={}", jti, e);
        }
    }

    @Override
    public boolean isBlacklisted(String jti) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_PREFIX + jti));
        } catch (Exception e) {
            log.error("Redis check failed for jti={}, treating as valid", jti, e);
            return false;
        }
    }
}
