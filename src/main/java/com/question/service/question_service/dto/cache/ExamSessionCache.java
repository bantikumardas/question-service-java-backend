package com.question.service.question_service.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSessionCache {

    private UUID sessionId;
    private UUID testId;
    private String testName;
    private UUID userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
