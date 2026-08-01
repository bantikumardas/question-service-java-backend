package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.TestQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamMcqResponse {

    private UUID questionId;
    private String question;
    private String questionImageUrl;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private Integer marks;
    private Integer orderIndex;
    private String level;
    // correctOption intentionally excluded — must not be exposed during exam

    public static ExamMcqResponse from(TestQuestion q) {
        return ExamMcqResponse.builder()
                .questionId(q.getQuestionId())
                .question(q.getQuestion())
                .questionImageUrl(q.getQuestionImageUrl())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .marks(q.getMarks())
                .orderIndex(q.getOrderIndex())
                .level(q.getLevel().name())
                .build();
    }
}
