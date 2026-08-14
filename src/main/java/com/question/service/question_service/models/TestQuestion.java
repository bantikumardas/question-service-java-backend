package com.question.service.question_service.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "test_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestQuestion {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "question_id", updatable = false, nullable = false)
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Lob
    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "question_image_url", length = 500)
    private String questionImageUrl;

    @Lob
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Lob
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Lob
    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Lob
    @Column(name = "option_d", nullable = false)
    private String optionD;

    @Enumerated(EnumType.STRING)
    @Column(name = "correct_option", nullable = false, length = 1)
    private Option correctOption;

    @Column(name = "marks", nullable = false)
    @Builder.Default
    private Integer marks = 1;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 10)
    @Builder.Default
    private Level level = Level.MEDIUM;

    public enum Option {
        A, B, C, D
    }

    public enum Level {
        EASY, MEDIUM, HARD
    }
}