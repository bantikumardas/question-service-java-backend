package com.question.service.question_service.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "test_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "test_case_id", updatable = false, nullable = false)
    private UUID testCaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_question_id", nullable = false)
    private CodingQuestion codingQuestion;

    @Column(name = "input", nullable = false, length = 1000)
    private String input;

    @Column(name = "expected_output", nullable = false, length = 1000)
    private String expectedOutput;

    @Lob
    @Column(name = "explanation")
    private String explanation;

    @Column(name = "is_hidden", nullable = false)
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "is_example", nullable = false)
    @Builder.Default
    private Boolean isExample = false;
}
