package com.question.service.question_service.dto.request;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreateCompanyRequest {
    private String companyName;
    private String emailDomain;
    private String phoneNumber;
    private String logoUrl;
    private String email;
}
