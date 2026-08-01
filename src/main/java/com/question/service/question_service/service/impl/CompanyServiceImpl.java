package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CreateCompanyRequest;
import com.question.service.question_service.dto.response.CompanyResponse;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.models.Company;
import com.question.service.question_service.models.User;
import com.question.service.question_service.repository.CompanyRepository;
import com.question.service.question_service.repository.UserRepository;
import com.question.service.question_service.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        String companyName = request.getCompanyName();
        String emailDomain = request.getEmailDomain();
        String phoneNumber = request.getPhoneNumber();
        if(companyRepository.findByEmailDomain(emailDomain) != null){
            throw new BadRequestException("Email domain already exists");
        }
        if(companyRepository.findByName(companyName) != null){
            throw new BadRequestException("Company name already exists");
        }
        User currentUser = getCurrentUser();
        if(currentUser.getRole() != User.Role.ADMIN){
            throw new BadRequestException("You are not authorized to perform this action");
        }
        Company company=Company.builder()
                .name(companyName)
                .emailDomain(emailDomain)
                .phone(phoneNumber)
                .companyKey(generateCompanyKey(4))
                .build();

        Company savedCompany=companyRepository.save(company);

        CompanyResponse response = CompanyResponse.builder()
                .id(savedCompany.getCompanyId())
                .companyName(savedCompany.getName())
                .emailDomain(savedCompany.getEmailDomain())
                .phoneNumber(savedCompany.getPhone())
                .companyKey(savedCompany.getCompanyKey())
                .build();
        return response;

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

    private String generateCompanyKey(int length) {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * alphanumeric.length());
            keyBuilder.append(alphanumeric.charAt(index));
        }
        return keyBuilder.toString();
    }
}
