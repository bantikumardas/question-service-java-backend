package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.cache.ExamSessionCache;
import com.question.service.question_service.dto.request.StartExamRequest;
import com.question.service.question_service.dto.response.ExamCodingResponse;
import com.question.service.question_service.dto.response.ExamMcqResponse;
import com.question.service.question_service.dto.response.StartExamResponse;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.exception.DuplicateResourceException;
import com.question.service.question_service.exception.ResourceNotFoundException;
import com.question.service.question_service.models.CodingQuestion;
import com.question.service.question_service.models.ExamSession;
import com.question.service.question_service.models.Test;
import com.question.service.question_service.models.TestCase;
import com.question.service.question_service.models.TestQuestion;
import com.question.service.question_service.repository.CodingQuestionRepository;
import com.question.service.question_service.repository.ExamSessionRepository;
import com.question.service.question_service.repository.TestCaseRepository;
import com.question.service.question_service.repository.TestQuestionRepository;
import com.question.service.question_service.repository.TestRepository;
import com.question.service.question_service.service.ExamService;
import com.question.service.question_service.service.ExamSessionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final CodingQuestionRepository codingQuestionRepository;
    private final TestCaseRepository testCaseRepository;
    private final ExamSessionRepository examSessionRepository;
    private final ExamSessionCacheService examSessionCacheService;

    @Override
    @Transactional
    public StartExamResponse startExam(StartExamRequest request) {
        Test test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + request.getTestId()));

        if (test.getStatus() != Test.Status.ACTIVE) {
            throw new BadRequestException("Test is not available for exam. Current status: " + test.getStatus());
        }

        // Check Redis first (fast path), fall back to DB if Redis is unavailable
        boolean hasActiveSession = examSessionCacheService.hasActiveSession(request.getUserId(), request.getTestId())
                || examSessionRepository.existsByTest_TestIdAndUserIdAndStatus(
                        request.getTestId(), request.getUserId(), ExamSession.SessionStatus.ACTIVE);

        if (hasActiveSession) {
            throw new DuplicateResourceException("User already has an active session for this test");
        }

        LocalDateTime endTime = LocalDateTime.now().plusSeconds(test.getTotalTimeSeconds());

        ExamSession session = ExamSession.builder()
                .test(test)
                .userId(request.getUserId())
                .endTime(endTime)
                .build();
        ExamSession savedSession = examSessionRepository.save(session);

        // Cache in Redis with TTL = exam duration so it auto-expires when exam ends
        ExamSessionCache cache = ExamSessionCache.builder()
                .sessionId(savedSession.getSessionId())
                .testId(test.getTestId())
                .testName(test.getTestName())
                .userId(request.getUserId())
                .startTime(savedSession.getStartTime())
                .endTime(savedSession.getEndTime())
                .status(savedSession.getStatus().name())
                .build();
        examSessionCacheService.save(cache, test.getTotalTimeSeconds());

        List<TestQuestion> mcqQuestions = testQuestionRepository
                .findByTest_TestIdOrderByOrderIndexAsc(request.getTestId());

        List<CodingQuestion> codingQuestions = codingQuestionRepository
                .findByTest_TestIdOrderByOrderIndexAsc(request.getTestId());

        Map<UUID, List<TestCase>> testCasesByQuestionId = fetchVisibleTestCases(codingQuestions);

        List<ExamMcqResponse> mcqResponses = mcqQuestions.stream()
                .map(ExamMcqResponse::from)
                .toList();

        List<ExamCodingResponse> codingResponses = codingQuestions.stream()
                .map(q -> ExamCodingResponse.from(
                        q, testCasesByQuestionId.getOrDefault(q.getCodingQuestionId(), List.of())))
                .toList();

        log.info("Started exam sessionId={} testId={} userId={}", savedSession.getSessionId(),
                request.getTestId(), request.getUserId());

        return StartExamResponse.builder()
                .sessionId(savedSession.getSessionId())
                .testId(test.getTestId())
                .testName(test.getTestName())
                .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                .startTime(savedSession.getStartTime())
                .endTime(savedSession.getEndTime())
                .status(savedSession.getStatus().name())
                .mcqQuestions(mcqResponses)
                .codingQuestions(codingResponses)
                .build();
    }

    private Map<UUID, List<TestCase>> fetchVisibleTestCases(List<CodingQuestion> codingQuestions) {
        if (codingQuestions.isEmpty()) {
            return Map.of();
        }
        List<UUID> codingIds = codingQuestions.stream()
                .map(CodingQuestion::getCodingQuestionId)
                .toList();
        return testCaseRepository.findByCodingQuestion_CodingQuestionIdInAndIsHiddenFalse(codingIds)
                .stream()
                .collect(Collectors.groupingBy(tc -> tc.getCodingQuestion().getCodingQuestionId()));
    }
}
