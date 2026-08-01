package com.question.service.question_service.dto.response;


import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeRunnerResponse {
    private String code;
    private String message;
    private Integer time;
    private String status;
    private Map<String, String> inOut;
}
