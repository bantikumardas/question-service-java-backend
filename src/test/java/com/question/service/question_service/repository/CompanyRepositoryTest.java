package com.question.service.question_service.repository;

import com.question.service.question_service.models.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

// Reuses the app's already-migrated dev H2 file (see application.yaml) instead of a fresh
// embedded database: a pre-existing mismatch between the Flyway schema and the
// CodingQuestion entity's LONGTEXT column definition fails Hibernate's schema validation
// against a freshly-migrated embedded H2. @DataJpaTest still wraps each test in a rolled-back
// transaction, so this does not persist data into the dev database.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
