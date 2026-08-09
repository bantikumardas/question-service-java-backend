package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.response.CompanyResponse;
import com.question.service.question_service.dto.response.UserProfile;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.models.Company;
import com.question.service.question_service.models.User;
import com.question.service.question_service.repository.UserRepository;
import com.question.service.question_service.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserProfile getUserProfile() {
       User currentUser = getCurrentUser();
       Company company = currentUser.getCompany();
       CompanyResponse companyResponse = company == null ? null : CompanyResponse.builder()
                 .id(company.getCompanyId())
                 .companyName(company.getName())
                 .emailDomain(company.getEmailDomain())
                 .phoneNumber(company.getPhone())
                 .companyKey(company.getCompanyKey())
                 .isActive(company.getIsActive())
                 .createdDate(company.getCreatedTime())
                 .updatedDate(company.getUpdatedTime())
                 .logoUrl(company.getLogoUrl())
                 .maxActiveTests(company.getMaxActiveTests())
                 .isSubscriptionActive(company.getIsSubscriptionActive())
                 .subscriptionStartDate(company.getSubscriptionStartDate())
                 .subscriptionEndDate(company.getSubscriptionExpiryDate())
                 .build();
       UserProfile userProfile=UserProfile.builder()
                 .id(currentUser.getUserId())
                 .name(currentUser.getName())
                 .email(currentUser.getEmail())
                 .role(currentUser.getRole())
                 .company(companyResponse)
                 .build();
       return userProfile;
    }

    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName == null || userName.isEmpty()) {
            throw new BadRequestException("You are not authorized to perform this action");
        }
        User user = userRepository.findByUserId(UUID.fromString(userName))
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userName));
        return user;
    }
}
