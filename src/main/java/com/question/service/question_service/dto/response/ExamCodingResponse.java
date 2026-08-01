package com.question.service.question_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.question.service.question_service.models.CodingQuestion;
import com.question.service.question_service.models.TestCase;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamCodingResponse {

    private UUID codingQuestionId;
    private String description;
    private List<String> paragraphs;
    private List<String> constraints;
    private String imageUrl1;
    private String imageUrl2;
    private String difficulty;
    private Integer marks;
    private Integer orderIndex;
    private List<ExamTestCaseResponse> sampleTestCases;

    public static ExamCodingResponse from(CodingQuestion q, List<TestCase> visibleTestCases) {
        return ExamCodingResponse.builder()
                .codingQuestionId(q.getCodingQuestionId())
                .description(q.getDescription())
                .paragraphs(q.getParagraphs())
                .constraints(q.getConstraints())
                .imageUrl1(q.getImageUrl1())
                .imageUrl2(q.getImageUrl2())
                .difficulty(q.getDifficulty().name())
                .marks(q.getMarks())
                .orderIndex(q.getOrderIndex())
                .sampleTestCases(visibleTestCases.stream().map(ExamTestCaseResponse::from).toList())
                .build();
    }
}
