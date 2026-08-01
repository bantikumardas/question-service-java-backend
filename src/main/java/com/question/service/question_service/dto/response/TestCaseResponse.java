package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.TestCase;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseResponse {

    private UUID testCaseId;
    private UUID codingQuestionId;
    private String input;
    private String expectedOutput;
    private String explanation;
    private Boolean isHidden;

    public static TestCaseResponse from(TestCase tc) {
        return TestCaseResponse.builder()
                .testCaseId(tc.getTestCaseId())
                .codingQuestionId(tc.getCodingQuestion().getCodingQuestionId())
                .input(tc.getInput())
                .expectedOutput(tc.getExpectedOutput())
                .explanation(tc.getExplanation())
                .isHidden(tc.getIsHidden())
                .build();
    }
}
