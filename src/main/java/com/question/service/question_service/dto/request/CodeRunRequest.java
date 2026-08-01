package com.question.service.question_service.dto.request;


import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
@Setter
public class CodeRunRequest {
    private Language language;
    private String code;
    private List<String> input;

    public enum Language{
        JAVA,PYTHON,JAVASCRIPT,TYPESCRIPT,C,CPP
    }
}
