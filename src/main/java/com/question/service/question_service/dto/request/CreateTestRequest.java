package com.question.service.question_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateTestRequest {

    @NotBlank(message = "Test name is required")
    @Size(max = 255, message = "Test name must not exceed 255 characters")
    private String testName;

    @NotNull(message = "Total time is required")
    @Min(value = 1, message = "Total time must be at least 1 minute")
    @Max(value = 480, message = "Total time must not exceed 480 minutes")
    private Integer totalTimeMinute;
}
