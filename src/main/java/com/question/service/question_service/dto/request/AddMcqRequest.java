package com.question.service.question_service.dto.request;

import com.question.service.question_service.models.TestQuestion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddMcqRequest {

    private UUID questionId;

    @NotNull(message = "Test ID is required")
    private UUID testId;

    @NotBlank(message = "Question is required")
    private String question;

    private String questionImageUrl;

    @NotBlank(message = "Option A is required")
    private String optionA;

    @NotBlank(message = "Option B is required")
    private String optionB;

    @NotBlank(message = "Option C is required")
    private String optionC;

    @NotBlank(message = "Option D is required")
    private String optionD;

    @NotNull(message = "Correct option is required")
    private TestQuestion.Option correctOption;

    @Min(value = 1, message = "Marks must be at least 1")
    private Integer marks = 1;

    @NotNull(message = "Level is required")
    private TestQuestion.Level level;
}
