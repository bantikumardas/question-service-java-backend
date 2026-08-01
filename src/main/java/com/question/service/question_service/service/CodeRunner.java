package com.question.service.question_service.service;

import com.question.service.question_service.dto.response.CodeRunnerResponse;

import java.io.IOException;

public interface CodeRunner {
    CodeRunnerResponse javaCodeRun(String code, String input, int timeoutSeconds) throws IOException;
    CodeRunnerResponse pythonCodeRun(String code, String input, int timeoutSeconds);
    CodeRunnerResponse javaScriptCodeRun(String code, String input, int timeoutSeconds);
    CodeRunnerResponse typeScriptCodeRun(String code, String input, int timeoutSeconds);
    CodeRunnerResponse cCodeRun(String code, String input, int timeoutSeconds);
    CodeRunnerResponse cppCodeRun(String code, String input, int timeoutSeconds);
}
