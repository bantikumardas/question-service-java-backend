package com.question.service.question_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class BulkUploadResponse {
    private UUID testId;
    private int totalRows;
    private int succeeded;
    private int failed;
    private List<String> errors;
}
