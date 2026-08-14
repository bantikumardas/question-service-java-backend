package com.question.service.question_service.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coding_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingQuestion {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "coding_question_id", updatable = false, nullable = false)
    private UUID codingQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "coding_question_paragraphs", joinColumns = @JoinColumn(name = "coding_question_id"))
    @Lob
    @Column(name = "paragraph")
    @OrderColumn(name = "paragraph_index")
    private List<String> paragraphs;

    @ElementCollection(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "constraint_text")
    @CollectionTable(name = "coding_question_constraints", joinColumns = @JoinColumn(name = "coding_question_id"))
    @OrderColumn(name = "constraint_index")
    private List<String> constraints;

    @Column(name = "image_url_1", length = 500)
    private String imageUrl1;

    @Column(name = "image_url_2", length = 500)
    private String imageUrl2;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    @Builder.Default
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(name = "marks", nullable = false)
    @Builder.Default
    private Integer marks = 10;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestCase> testCases;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}