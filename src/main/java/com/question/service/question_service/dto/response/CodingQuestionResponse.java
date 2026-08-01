package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.CodingQuestion;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodingQuestionResponse {

    private UUID codingQuestionId;
    private UUID testId;
    private String title;
    private String description;
    private List<String> paragraphs;
    private List<String> constraints;
    private String imageUrl1;
    private String imageUrl2;
    private String difficulty;
    private Integer marks;
    private Integer orderIndex;

    public static CodingQuestionResponse from(CodingQuestion q) {
        return CodingQuestionResponse.builder()
                .codingQuestionId(q.getCodingQuestionId())
                .testId(q.getTest().getTestId())
                .title(q.getTitle())
                .description(q.getDescription())
                .paragraphs(q.getParagraphs())
                .constraints(q.getConstraints())
                .imageUrl1(q.getImageUrl1())
                .imageUrl2(q.getImageUrl2())
                .difficulty(q.getDifficulty().name())
                .marks(q.getMarks())
                .orderIndex(q.getOrderIndex())
                .build();
    }
}
