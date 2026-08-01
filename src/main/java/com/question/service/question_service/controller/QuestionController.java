package com.question.service.question_service.controller;

import com.question.service.question_service.dto.request.AddCodingQuestionRequest;
import com.question.service.question_service.dto.request.AddMcqRequest;
import com.question.service.question_service.dto.request.AddTestCaseRequest;
import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.CodingQuestionResponse;
import com.question.service.question_service.dto.response.McqResponse;
import com.question.service.question_service.dto.response.TestCaseResponse;
import com.question.service.question_service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/one/mcq")
    public ResponseEntity<ApiResponse<McqResponse>> addMcq(@Valid @RequestBody AddMcqRequest request) {
        McqResponse response = questionService.addMcq(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("MCQ question added successfully", response));
    }

    @PostMapping("/one/coding")
    public ResponseEntity<ApiResponse<CodingQuestionResponse>> addCodingQuestion(
            @Valid @RequestBody AddCodingQuestionRequest request) {
        CodingQuestionResponse response = questionService.addCodingQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coding question added successfully", response));
    }

    @PostMapping("/one/testcase")
    public ResponseEntity<ApiResponse<TestCaseResponse>> addTestCase(
            @Valid @RequestBody AddTestCaseRequest request) {
        TestCaseResponse response = questionService.addTestCase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test case added successfully", response));
    }

}
