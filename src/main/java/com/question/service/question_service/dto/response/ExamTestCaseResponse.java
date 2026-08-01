package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.TestCase;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamTestCaseResponse {

    private UUID testCaseId;
    private String input;
    private String expectedOutput;
    private String explanation;

    public static ExamTestCaseResponse from(TestCase tc) {
        return ExamTestCaseResponse.builder()
                .testCaseId(tc.getTestCaseId())
                .input(tc.getInput())
                .expectedOutput(tc.getExpectedOutput())
                .explanation(tc.getExplanation())
                .build();
    }
}
