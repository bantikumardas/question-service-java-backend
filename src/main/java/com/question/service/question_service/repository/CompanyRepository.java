package com.question.service.question_service.repository;

import com.question.service.question_service.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByEmailDomain(String emailDomain);

    Company findByEmailDomain(String emailDomain);

    Company findByName(String companyName);

}
