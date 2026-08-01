package com.question.service.question_service.service.impl;

import com.question.service.question_service.models.TestInvite;
import com.question.service.question_service.repository.TestInviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Each method commits in its own transaction so one invite's outcome
 * never blocks or rolls back another's while the dispatch pool runs in parallel.
 */
@Component
@RequiredArgsConstructor
public class TestInviteStatusUpdater {

    private final TestInviteRepository testInviteRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TestInvite markInProgress(UUID inviteId) {
        return testInviteRepository.findById(inviteId)
                .filter(invite -> invite.getStatus() == TestInvite.Status.PENDING
                        || invite.getStatus() == TestInvite.Status.FAILED)
                .map(invite -> {
                    invite.setStatus(TestInvite.Status.INPROGRESS);
                    invite.setAttemptCount(invite.getAttemptCount() + 1);
                    return testInviteRepository.save(invite);
                })
                .orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSuccess(UUID inviteId) {
        testInviteRepository.findById(inviteId).ifPresent(invite -> {
            invite.setStatus(TestInvite.Status.SUCCESS);
            invite.setFailureReason(null);
            testInviteRepository.save(invite);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID inviteId, String reason) {
        testInviteRepository.findById(inviteId).ifPresent(invite -> {
            invite.setStatus(TestInvite.Status.FAILED);
            invite.setFailureReason(reason);
            testInviteRepository.save(invite);
        });
    }
}
