package com.question.service.question_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

// Replaces the real file-backed H2 datasource (~/.question-service/question_db) with an
// embedded one: the file-mode AUTO_SERVER datasource does not reliably expose CLOB column
// metadata to Hibernate's schema validator immediately after Flyway migrates it within the
// same process, and tests shouldn't write to the developer's real dev database anyway.
@AutoConfigureTestDatabase
@SpringBootTest
class QuestionServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
