package com.question.service.question_service.controller;

import com.question.service.question_service.dto.request.CreateTestRequest;
import com.question.service.question_service.dto.request.SendTestInvite;
import com.question.service.question_service.dto.response.AdminTestResponse;
import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.PagedResponse;
import com.question.service.question_service.dto.response.TestResponse;
import com.question.service.question_service.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TestResponse>> createTest(@Valid @RequestBody CreateTestRequest request) {
        TestResponse response = testService.createTest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test created successfully", response));
    }

    @GetMapping("/all-tests")
    public ResponseEntity<ApiResponse<PagedResponse<TestResponse>>> getAllTests(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdTime") String sortBy,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value="sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "companyId", required = false) UUID companyId
    ) {
        // Implement logic to retrieve all tests
        Page<TestResponse> responses=testService.getAllTest(page, size, sortBy, status, sortDir, query, companyId);
        return ResponseEntity.ok(ApiResponse.success("Tests retrieved successfully", PagedResponse.from(responses)));
    }

    @GetMapping("/{testId}")
    public  ResponseEntity<ApiResponse<AdminTestResponse>> getTestById(@PathVariable String testId) {
        AdminTestResponse adminTestResponse= testService.getAllQuestionFromTest(testId);
        return ResponseEntity.ok(ApiResponse.success("Test retrieved successfully", adminTestResponse));
    }

    @PostMapping("/{testId}/change-status")
    public ResponseEntity<ApiResponse<String>> changeTestStatus(@PathVariable UUID testId, @RequestParam String targetStatus) {
        System.out.println("Target status : "+targetStatus);
        testService.changeTestStatus(testId, targetStatus);
        return ResponseEntity.ok(ApiResponse.success("Success", "Change status successfully"));
    }

    @PostMapping("send/invite/{testId}")
    public ResponseEntity<ApiResponse<String>> sendInvite(@PathVariable UUID testId, @RequestBody SendTestInvite invite) {
        System.out.println("Send Invite api is called");
        String response=testService.sendTestInvite(testId, invite.getEmails());
        return ResponseEntity.ok(ApiResponse.success("Success", response));
    }

}
