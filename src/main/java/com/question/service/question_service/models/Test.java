package com.question.service.question_service.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test implements Comparable<Test>{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "test_id", updatable = false, nullable = false)
    private UUID testId;

    @Column(name = "test_name", nullable = false, length = 255)
    private String testName;

    @Column(name = "total_time_seconds", nullable = false)
    private Long totalTimeSeconds;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<TestQuestion> testQuestions;


    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<CodingQuestion> codingQuestions;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

    @Column(name = "is_created_by_admin", nullable = false)
    private Boolean isCreatedByAdmin;

    @Column(name = "is_created_by_ca_admin", nullable = false)
    private Boolean isCreatedByCAAdmin;

    @Override
    public int compareTo(Test o) {
        return Integer.compare(this.status.getPriority(), o.status.getPriority());
    }


    @Getter
    @AllArgsConstructor
    public enum Status {
        ACTIVE(0), DRAFT(1), ARCHIVED(2);

        private final int priority;
    }
}