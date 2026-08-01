package com.question.service.question_service.controller;

import com.question.service.question_service.dto.request.StartExamRequest;
import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.StartExamResponse;
import com.question.service.question_service.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(@Valid @RequestBody StartExamRequest request) {
        StartExamResponse response = examService.startExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam started successfully", response));
    }
}
