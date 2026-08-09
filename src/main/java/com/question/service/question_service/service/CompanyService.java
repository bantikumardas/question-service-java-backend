package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.CreateCompanyRequest;
import com.question.service.question_service.dto.response.CompanyResponse;
import java.util.List;


public interface CompanyService {
        CompanyResponse createCompany(CreateCompanyRequest request);
        List<CompanyResponse> getAllCompanies();
}
