package com.question.service.question_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class StartExamResponse {

    private UUID sessionId;
    private UUID testId;
    private String testName;
    private Integer totalTimeMinute;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private List<ExamMcqResponse> mcqQuestions;
    private List<ExamCodingResponse> codingQuestions;
}
