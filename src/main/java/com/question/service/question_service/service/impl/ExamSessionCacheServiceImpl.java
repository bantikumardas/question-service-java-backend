package com.question.service.question_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.question.service.question_service.dto.cache.ExamSessionCache;
import com.question.service.question_service.service.ExamSessionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamSessionCacheServiceImpl implements ExamSessionCacheService {

    private static final String KEY_PREFIX = "exam:session:";

    private final StringRedisTemplate stringRedisTemplate;

    @Qualifier("redisObjectMapper")
    private final ObjectMapper objectMapper;

    @Override
    public void save(ExamSessionCache session, long ttlSeconds) {
        String key = buildKey(session.getUserId(), session.getTestId());
        try {
            String json = objectMapper.writeValueAsString(session);
            stringRedisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Cached exam session key={} ttl={}s", key, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to cache exam session key={}", key, e);
        }
    }

    @Override
    public boolean hasActiveSession(UUID userId, UUID testId) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(buildKey(userId, testId)));
        } catch (Exception e) {
            log.error("Redis check failed for userId={} testId={}, falling back to DB", userId, testId, e);
            return false;
        }
    }

    @Override
    public Optional<ExamSessionCache> get(UUID userId, UUID testId) {
        String key = buildKey(userId, testId);
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, ExamSessionCache.class));
        } catch (Exception e) {
            log.error("Failed to read exam session from Redis key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public void delete(UUID userId, UUID testId) {
        try {
            stringRedisTemplate.delete(buildKey(userId, testId));
            log.debug("Deleted exam session from Redis userId={} testId={}", userId, testId);
        } catch (Exception e) {
            log.error("Failed to delete exam session from Redis userId={} testId={}", userId, testId, e);
        }
    }

    private String buildKey(UUID userId, UUID testId) {
        return KEY_PREFIX + userId + ":" + testId;
    }
}
