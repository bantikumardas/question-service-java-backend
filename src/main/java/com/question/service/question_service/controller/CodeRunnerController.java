package com.question.service.question_service.controller;


import com.question.service.question_service.dto.request.CodeRunRequest;
import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.CodeRunnerResponse;
import com.question.service.question_service.service.CodeRunService;
import com.question.service.question_service.service.CodeRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/code-run")
@RequiredArgsConstructor
public class CodeRunnerController {

    @Autowired
    private CodeRunService codeRunner;

    @PostMapping("/run")
    public ResponseEntity<ApiResponse<CodeRunnerResponse>> run(@RequestBody CodeRunRequest request) {
        CodeRunnerResponse response=codeRunner.run(request);
        return ResponseEntity.ok(ApiResponse.success("Run Completed", response));
    }
}
