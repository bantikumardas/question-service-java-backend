package com.question.service.question_service.service;

import com.question.service.question_service.dto.cache.ExamSessionCache;

import java.util.Optional;
import java.util.UUID;

public interface ExamSessionCacheService {

    void save(ExamSessionCache session, long ttlSeconds);

    boolean hasActiveSession(UUID userId, UUID testId);

    Optional<ExamSessionCache> get(UUID userId, UUID testId);

    void delete(UUID userId, UUID testId);
}
