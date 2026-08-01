package com.question.service.question_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StartExamRequest {

    @NotNull(message = "Test ID is required")
    private UUID testId;

    @NotNull(message = "User ID is required")
    private UUID userId;
}
