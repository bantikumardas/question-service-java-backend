package com.question.service.question_service.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "company_id", updatable = false, nullable = false)
    private UUID companyId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email_domain", unique = true, nullable = false)
    private String emailDomain;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "company-key", unique = true, nullable = false)
    private String companyKey;

}
