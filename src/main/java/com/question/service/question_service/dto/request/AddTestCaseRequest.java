package com.question.service.question_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddTestCaseRequest {

    @NotNull(message = "Coding question ID is required")
    private UUID codingQuestionId;

    @NotBlank(message = "Input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    private String explanation;

    private Boolean isHidden = false;

    private Boolean isExample = false;
}
