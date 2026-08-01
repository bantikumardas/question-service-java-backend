package com.question.service.question_service.dto.response;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AdminTestResponse {
    private UUID testId;
    private String testName;
    private Integer totalTimeMinute;
    private UUID createdBy;
    private LocalDateTime createdTime;
    private String status;
    private Integer totalQuestions;
    private Integer totalCodingQuestions;
    private List<McqResponse> mcqQuestions;
    private List<CodingQuestionResponse>  codingQuestionResponses;


}
