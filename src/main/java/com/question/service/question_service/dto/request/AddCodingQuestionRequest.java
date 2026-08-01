package com.question.service.question_service.dto.request;

import com.question.service.question_service.models.CodingQuestion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AddCodingQuestionRequest {

    private UUID codingQuestionId;

    @NotNull(message = "Test ID is required")
    private UUID testId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private List<String> paragraphs;

    @NotNull
    @Size(min = 1, message = "At least one constraint is required")
    private List<String> constraints;

    private String imageUrl1;

    private String imageUrl2;

    @NotNull
    @Size(min = 2, message = "At least two example test case is required")
    private List<AddTestCaseRequest> testCases;

    private CodingQuestion.Difficulty difficulty = CodingQuestion.Difficulty.MEDIUM;

    @Min(value = 1, message = "Marks must be at least 1")
    private Integer marks = 10;
}
