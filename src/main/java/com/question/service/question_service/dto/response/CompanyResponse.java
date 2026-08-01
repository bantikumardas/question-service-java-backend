package com.question.service.question_service.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyResponse {
    private UUID id;
    private String companyName;
    private String emailDomain;
    private String phoneNumber;
    private String companyKey;
}
