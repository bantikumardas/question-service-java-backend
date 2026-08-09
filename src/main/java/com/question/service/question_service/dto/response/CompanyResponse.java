package com.question.service.question_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
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
    private String logoUrl;
    private int maxActiveTests;
    private boolean isSubscriptionActive;
    private boolean isActive;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
