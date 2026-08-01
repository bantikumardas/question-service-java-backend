package com.question.service.question_service.repository;

import com.question.service.question_service.models.ExamSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamSessionRepository extends JpaRepository<ExamSession, UUID> {

    boolean existsByTest_TestIdAndUserIdAndStatus(UUID testId, UUID userId, ExamSession.SessionStatus status);
}
