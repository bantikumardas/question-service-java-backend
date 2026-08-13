package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.CreateCompanyRequest;
import com.question.service.question_service.dto.response.CompanyResponse;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.models.Company;
import com.question.service.question_service.models.User;
import com.question.service.question_service.repository.CompanyRepository;
import com.question.service.question_service.repository.UserRepository;
import com.question.service.question_service.service.impl.CompanyServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @AfterEach
    void tearDown() {
        if (securityContextHolderMock != null) {
            securityContextHolderMock.close();
            securityContextHolderMock = null;
        }
    }

    private void mockCurrentUser(User user) {
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getUserId().toString());
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
    }

    @Test
    void createCompany_throwsWhenEmailDomainAlreadyExists() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .build();

        when(companyRepository.findByEmailDomain("acme.com")).thenReturn(new Company());

        assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email domain already exists");

        verify(companyRepository, never()).save(any());
    }

    @Test
    void createCompany_throwsWhenCompanyNameAlreadyExists() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .build();

        when(companyRepository.findByEmailDomain("acme.com")).thenReturn(null);
        when(companyRepository.findByName("Acme")).thenReturn(new Company());

        assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Company name already exists");

        verify(companyRepository, never()).save(any());
    }

    @Test
    void createCompany_throwsWhenCurrentUserIsNotAdmin() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .build();

        when(companyRepository.findByEmailDomain("acme.com")).thenReturn(null);
        when(companyRepository.findByName("Acme")).thenReturn(null);

        User candidate = User.builder().userId(UUID.randomUUID()).role(User.Role.CANDIDATE).build();
        mockCurrentUser(candidate);

        assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You are not authorized to perform this action");

        verify(companyRepository, never()).save(any());
    }

    @Test
    void createCompany_savesAndReturnsResponseWhenAdmin() {
        CreateCompanyRequest request = CreateCompanyRequest.builder()
                .companyName("Acme")
                .emailDomain("acme.com")
                .phoneNumber("9999999999")
                .email("info@acme.com")
                .logoUrl("logo.png")
                .build();

        when(companyRepository.findByEmailDomain("acme.com")).thenReturn(null);
        when(companyRepository.findByName("Acme")).thenReturn(null);

        User admin = User.builder().userId(UUID.randomUUID()).role(User.Role.ADMIN).build();
        mockCurrentUser(admin);

        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company toSave = invocation.getArgument(0);
            toSave.setCompanyId(UUID.randomUUID());
            return toSave;
        });

        CompanyResponse response = companyService.createCompany(request);

        assertThat(response.getCompanyName()).isEqualTo("Acme");
        assertThat(response.getEmailDomain()).isEqualTo("acme.com");
        assertThat(response.getCompanyKey()).hasSize(4);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void getAllCompanies_throwsWhenCurrentUserIsNotAdmin() {
        User candidate = User.builder().userId(UUID.randomUUID()).role(User.Role.CANDIDATE).build();
        mockCurrentUser(candidate);

        assertThatThrownBy(() -> companyService.getAllCompanies())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("You are not authorized to perform this action");

        verify(companyRepository, never()).findAll();
    }

    @Test
    void getAllCompanies_returnsAllCompaniesWhenAdmin() {
        User admin = User.builder().userId(UUID.randomUUID()).role(User.Role.ADMIN).build();
        mockCurrentUser(admin);

        Company company = Company.builder()
                .companyId(UUID.randomUUID())
                .name("Acme")
                .emailDomain("acme.com")
                .phone("9999999999")
                .companyKey("ABCD")
                .build();
        when(companyRepository.findAll()).thenReturn(List.of(company));

        List<CompanyResponse> responses = companyService.getAllCompanies();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCompanyName()).isEqualTo("Acme");
    }
}
