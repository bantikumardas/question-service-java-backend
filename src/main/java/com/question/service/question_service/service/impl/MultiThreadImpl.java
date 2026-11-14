package com.question.service.question_service.service.impl;

import com.question.service.question_service.exception.ResourceNotFoundException;
import com.question.service.question_service.models.Test;
import com.question.service.question_service.models.TestQuestion;
import com.question.service.question_service.repository.TestQuestionRepository;
import com.question.service.question_service.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Runs as its own bean (not a method on TestServiceImpl) so that @Async is actually
 * honored by the Spring AOP proxy - see TestInviteDispatcher for the same reasoning.
 * Each row runs on its own thread from "bulkUploadExecutor" with its own transaction,
 * so one bad row can't roll back or block the others.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiThreadImpl {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;

    @Async("bulkUploadExecutor")
    @Transactional
    public CompletableFuture<BulkUploadRowResult> processRow(BulkMcqRow row, UUID testId) {
        try {
            TestQuestion.Option correctOption;
            try {
                correctOption = TestQuestion.Option.valueOf(row.correctOption().trim().toUpperCase());
            } catch (Exception e) {
                return CompletableFuture.completedFuture(
                        BulkUploadRowResult.failure(row.rowNumber(), "Invalid correct_option '" + row.correctOption() + "'"));
            }

            TestQuestion.Level level;
            try {
                level = TestQuestion.Level.valueOf(row.level().trim().toUpperCase());
            } catch (Exception e) {
                return CompletableFuture.completedFuture(
                        BulkUploadRowResult.failure(row.rowNumber(), "Invalid level '" + row.level() + "'"));
            }

            TestQuestion entity;
            if (row.questionId() != null) {
                entity = testQuestionRepository.findById(row.questionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + row.questionId()));
                if (!entity.getTest().getTestId().equals(testId)) {
                    return CompletableFuture.completedFuture(BulkUploadRowResult.failure(row.rowNumber(),
                            "Question " + row.questionId() + " does not belong to test " + testId));
                }
            } else {
                Test test = testRepository.findById(testId)
                        .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));
                entity = new TestQuestion();
                entity.setTest(test);
                entity.setOrderIndex(row.orderIndex());
            }

            entity.setQuestion(row.question());
            entity.setOptionA(row.optionA());
            entity.setOptionB(row.optionB());
            entity.setOptionC(row.optionC());
            entity.setOptionD(row.optionD());
            entity.setCorrectOption(correctOption);
            entity.setLevel(level);
            entity.setMarks(row.marks());

            TestQuestion saved = testQuestionRepository.save(entity);
            return CompletableFuture.completedFuture(BulkUploadRowResult.success(row.rowNumber(), saved.getQuestionId()));
        } catch (Exception e) {
            log.warn("Bulk upload row {} failed: {}", row.rowNumber(), e.getMessage());
            return CompletableFuture.completedFuture(BulkUploadRowResult.failure(row.rowNumber(), e.getMessage()));
        }
    }
}
