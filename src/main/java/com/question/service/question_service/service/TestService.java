package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.CreateTestRequest;
import com.question.service.question_service.dto.response.AdminTestResponse;
import com.question.service.question_service.dto.response.TestResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TestService {

    TestResponse createTest(CreateTestRequest request);

    Page<TestResponse> getAllTest(int page, int size, String sortBy, String status, String sortDir, String query);

    AdminTestResponse getAllQuestionFromTest(String testId);

    void changeTestStatus(UUID testId, String targetStatus);

    String sendTestInvite(UUID testId, String emails);

}
