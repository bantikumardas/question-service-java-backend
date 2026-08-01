package com.question.service.question_service.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunicatonRequest {
    private UUID userId;
    private String message;
    private String subject;
    private String purpose;
    private String emailOrPhoneNumber;
}
