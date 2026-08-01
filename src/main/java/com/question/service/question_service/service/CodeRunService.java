package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.CodeRunRequest;
import com.question.service.question_service.dto.response.CodeRunnerResponse;

public interface CodeRunService {
    CodeRunnerResponse run(CodeRunRequest request);
}
