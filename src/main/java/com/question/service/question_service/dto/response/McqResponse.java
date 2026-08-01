package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.TestQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McqResponse {

    private UUID questionId;
    private UUID testId;
    private String question;
    private String questionImageUrl;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private Integer marks;
    private Integer orderIndex;
    private String level;

    public static McqResponse from(TestQuestion q) {
        return McqResponse.builder()
                .questionId(q.getQuestionId())
                .testId(q.getTest().getTestId())
                .question(q.getQuestion())
                .questionImageUrl(q.getQuestionImageUrl())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .correctOption(q.getCorrectOption().name())
                .marks(q.getMarks())
                .orderIndex(q.getOrderIndex())
                .level(q.getLevel().name())
                .build();
    }
}
