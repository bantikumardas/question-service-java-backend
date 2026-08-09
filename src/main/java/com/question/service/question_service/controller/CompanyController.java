package com.question.service.question_service.controller;


import com.question.service.question_service.dto.request.CreateCompanyRequest;
import com.question.service.question_service.dto.response.ApiResponse;
import com.question.service.question_service.dto.response.CompanyResponse;
import com.question.service.question_service.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@RequestBody CreateCompanyRequest createCompanyRequest) {
        CompanyResponse companyResponse = companyService.createCompany(createCompanyRequest);
        return ResponseEntity.ok(ApiResponse.success("Company created successfully", companyResponse));
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAllCompanies() {
        List<CompanyResponse> companyResponses = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("Companies fetched successfully", companyResponses));
    }
}
