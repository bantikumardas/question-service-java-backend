package com.question.service.question_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.question.service.question_service.config.SecurityConfig;
import com.question.service.question_service.dto.request.CreateCompanyRequest;
import com.question.service.question_service.dto.response.CompanyResponse;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.security.JwtAuthFilter;
import com.question.service.question_service.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CompanyController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthFilter.class}))
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    @Test
    void createCompany_returnsCreatedCompany() throws Exception {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .build();

        CompanyResponse response = CompanyResponse.builder()
                .id(UUID.randomUUID())
                .companyName("Acme")
                .emailDomain("acme.com")
                .build();

        when(companyService.createCompany(any(CreateCompanyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/company/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyName").value("Acme"));
    }

    @Test
    void createCompany_returnsBadRequestWhenServiceThrows() throws Exception {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .build();

        when(companyService.createCompany(any(CreateCompanyRequest.class)))
                .thenThrow(new BadRequestException("Company name already exists"));

        mockMvc.perform(post("/api/company/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Company name already exists"));
    }

    @Test
    void getAllCompanies_returnsListOfCompanies() throws Exception {
        CompanyResponse response = CompanyResponse.builder()
                .id(UUID.randomUUID())
                .companyName("Acme")
                .emailDomain("acme.com")
                .build();

        when(companyService.getAllCompanies()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/company/fetch/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].companyName").value("Acme"));
    }
}
