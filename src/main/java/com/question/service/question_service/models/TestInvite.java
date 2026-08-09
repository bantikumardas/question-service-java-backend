package com.question.service.question_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="test_invite")
public class TestInvite {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "invite_id", updatable = false, nullable = false)
    private UUID inviteId;

    @ManyToOne
    @JoinColumn(name = "test_test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private User invitedBy;

    @Column
    private String email;

    @Column(name = "invitation_code", unique = true)
    private String invitationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "invitation_sent_time", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime invitationSentTime;

    @Column(name = "updated_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime updatedTime;

    public enum Status{
        PENDING, INPROGRESS, SUCCESS, FAILED
    }


}
