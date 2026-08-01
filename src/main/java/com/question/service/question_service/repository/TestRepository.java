package com.question.service.question_service.repository;

import com.question.service.question_service.models.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {

    boolean existsByTestName(String testName);

    Page<Test> findByTestNameContainingIgnoreCase(String query, Pageable pageable);
}
