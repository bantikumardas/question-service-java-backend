package com.question.service.question_service.dto.request;

import com.question.service.question_service.dto.response.CodingQuestionResponse;
import com.question.service.question_service.dto.response.McqResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateTestQuestions {
    private UUID testId;
    private List<McqResponse> mcqQuestions;
    private List<CodingQuestionResponse>  codingQuestionResponses;
}
