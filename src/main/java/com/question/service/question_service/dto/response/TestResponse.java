package com.question.service.question_service.dto.response;

import com.question.service.question_service.models.Test;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TestResponse {

    private UUID testId;
    private String testName;
    private Integer totalTimeMinute;
    private UUID createdBy;
    private LocalDateTime createdTime;
    private String status;
    private Integer totalQuestions;
    private Integer totalCodingQuestions;

    public static TestResponse from(Test test) {
        return TestResponse.builder()
                .testId(test.getTestId())
                .testName(test.getTestName())
                .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                .createdBy(test.getCreatedBy())
                .createdTime(test.getCreatedTime())
                .status(test.getStatus().name())
                .build();
    }
}
