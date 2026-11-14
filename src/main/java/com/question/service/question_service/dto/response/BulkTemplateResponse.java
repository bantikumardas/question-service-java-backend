package com.question.service.question_service.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@Data
public class BulkTemplateResponse {
    private UUID fileId;
    private UUID testId;
    private String name;
    private String type;
    private String content;
    private String path;
}
