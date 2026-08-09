package com.question.service.question_service.dto.response;


import com.question.service.question_service.models.User;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    private UUID id;
    private String name;
    private String email;
    private User.Role role;
    private CompanyResponse company;
}
