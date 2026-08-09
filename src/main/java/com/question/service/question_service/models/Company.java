package com.question.service.question_service.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
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

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(name = "logo_url", nullable = true)
    private String logoUrl;

    @Column(name = "email",  unique = true)
    private String email;

    @Column(name = "max_active_tests", nullable = false)
    @Builder.Default
    private Integer maxActiveTests=1;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean isActive=true;

    @Column(name = "subscription_active", nullable = false)
    @Builder.Default
    private Boolean isSubscriptionActive=false;

    @Column(name = "subscription_expiry_date", nullable = true)
    private LocalDateTime subscriptionExpiryDate;

    @Column(name = "subscription_start_date", nullable = true)
    private LocalDateTime subscriptionStartDate;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false, nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedTime;
}
