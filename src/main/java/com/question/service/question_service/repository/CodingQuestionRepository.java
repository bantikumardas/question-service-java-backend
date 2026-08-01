package com.question.service.question_service.repository;

import com.question.service.question_service.models.CodingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CodingQuestionRepository extends JpaRepository<CodingQuestion, UUID> {

    int countByTest_TestId(UUID testId);

    List<CodingQuestion> findByTest_TestIdOrderByOrderIndexAsc(UUID testId);
}
