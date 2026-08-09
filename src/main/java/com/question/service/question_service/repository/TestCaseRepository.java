package com.question.service.question_service.repository;

import com.question.service.question_service.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    List<TestCase> findByCodingQuestion_CodingQuestionIdInAndIsHiddenFalse(List<UUID> codingQuestionIds);

    List<TestCase> findByCodingQuestion_CodingQuestionId(UUID codingQuestionId);


    void deleteByCodingQuestion_CodingQuestionId(UUID codingQuestionId);
}
