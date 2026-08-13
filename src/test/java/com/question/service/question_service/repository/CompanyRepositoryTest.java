package com.question.service.question_service.repository;

import com.question.service.question_service.models.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    private Company newCompany(String name, String emailDomain, String companyKey) {
        return Company.builder()
                .name(name)
                .emailDomain(emailDomain)
                .phone("9999999999")
                .companyKey(companyKey)
                .email(companyKey + "@" + emailDomain)
                .build();
    }

    @Test
    void findByEmailDomain_returnsMatchingCompany() {
        companyRepository.save(newCompany("Acme", "acme.com", "ABCD"));

        Company found = companyRepository.findByEmailDomain("acme.com");

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Acme");
    }

    @Test
    void findByEmailDomain_returnsNullWhenNoMatch() {
        Company found = companyRepository.findByEmailDomain("missing.com");

        assertThat(found).isNull();
    }

    @Test
    void findByName_returnsMatchingCompany() {
        companyRepository.save(newCompany("Acme", "acme.com", "ABCD"));

        Company found = companyRepository.findByName("Acme");

        assertThat(found).isNotNull();
        assertThat(found.getEmailDomain()).isEqualTo("acme.com");
    }

    @Test
    void existsByEmailDomain_reflectsSavedState() {
        assertThat(companyRepository.existsByEmailDomain("acme.com")).isFalse();

        companyRepository.save(newCompany("Acme", "acme.com", "ABCD"));

        assertThat(companyRepository.existsByEmailDomain("acme.com")).isTrue();
    }
}
