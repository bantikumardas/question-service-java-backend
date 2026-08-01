package com.question.service.question_service.repository;

import com.question.service.question_service.models.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, UUID> {

    int countByTest_TestId(UUID testId);

    List<TestQuestion> findByTest_TestIdOrderByOrderIndexAsc(UUID testId);
}
