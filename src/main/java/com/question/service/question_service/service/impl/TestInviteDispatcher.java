package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CommunicatonRequest;
import com.question.service.question_service.models.Test;
import com.question.service.question_service.models.TestInvite;
import com.question.service.question_service.models.User;
import com.question.service.question_service.repository.TestInviteRepository;
import com.question.service.question_service.service.CommunicationManger;
import com.question.service.question_service.utils.TestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Runs as its own bean (not a method on TestServiceImpl) so that @Async is actually
 * honored by the Spring AOP proxy - calling it from within the same class as
 * sendTestInvite would bypass the proxy and run synchronously on the request thread.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestInviteDispatcher {

    private static final String CODE_ALPHABET = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_CODE_GENERATION_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TestInviteRepository testInviteRepository;
    private final TestInviteStatusUpdater testInviteStatusUpdater;
    private final CommunicationManger communicationManger;
    private final TestUtils testUtils;

    @Value("${invite.max-retry}")
    private int maxRetry;

    @Async("emailExecutor")
    public void dispatch(User invitedBy, Test test, Set<String> emails) {
        Map<String, TestInvite> existingInvites = testInviteRepository
                .findByTest_TestIdAndEmailIn(test.getTestId(), emails)
                .stream()
                .collect(Collectors.toMap(TestInvite::getEmail, invite -> invite));
        System.out.println("Running dispatch function");
        for (String email : emails) {
            try {
                System.out.println("Calling dispatchOne for : "+email);
                dispatchOne(invitedBy, test, email, existingInvites.get(email));
            } catch (Exception e) {
                log.error("Unexpected error dispatching invite for test={} to {}: {}",
                        test.getTestId(), email, e.getMessage(), e);
            }
        }
    }

    private void dispatchOne(User invitedBy, Test test, String email, TestInvite existing) {
        TestInvite testInvite = existing != null ? existing : createPendingInvite(invitedBy, test, email);
        System.out.println("Sending email for "+email);
        TestInvite inProgress = testInviteStatusUpdater.markInProgress(testInvite.getInviteId());
        if (inProgress == null) {
            log.debug("Skipping invite {} for {} - already succeeded or currently in progress",
                    testInvite.getInviteId(), email);
            //TODO: For testing always send email
//            return;
        }

        CommunicatonRequest request = new CommunicatonRequest();
        request.setEmailOrPhoneNumber(email);
        request.setSubject("Test Invite for " + test.getTestName());
        request.setMessage(testUtils.getInvitationHtmlCode(test, inProgress));

        boolean sent = false;
        String failureReason = null;

        for (int attempt = 1; attempt <= maxRetry && !sent; attempt++) {
            try {
                sent = communicationManger.emailCommunication(request);
                if (!sent) {
                    failureReason = "Email rejected by mail server";
                }
            } catch (Exception e) {
                failureReason = e.getMessage();
                log.warn("Attempt {}/{} failed sending invite to {}: {}", attempt, maxRetry, email, failureReason);
            }
            if (!sent && attempt < maxRetry) {
                sleep(backoffSeconds(attempt));
            }
        }

        if (sent) {
            testInviteStatusUpdater.markSuccess(inProgress.getInviteId());
        } else {
            testInviteStatusUpdater.markFailed(inProgress.getInviteId(),
                    failureReason != null ? failureReason : "Unable to send email after " + maxRetry + " attempts");
        }
    }

    private TestInvite createPendingInvite(User invitedBy, Test test, String email) {
        TestInvite invite = TestInvite.builder()
                .email(email)
                .test(test)
                .invitedBy(invitedBy)
                .invitationCode(generateUniqueInvitationCode())
                .status(TestInvite.Status.PENDING)
                .attemptCount(0)
                .build();
        return saveWithUniqueCode(invite);
    }

    private TestInvite saveWithUniqueCode(TestInvite invite) {
        for (int i = 0; i < MAX_CODE_GENERATION_ATTEMPTS; i++) {
            try {
                return testInviteRepository.save(invite);
            } catch (DataIntegrityViolationException e) {
                log.warn("Invitation code collision while saving invite for {}, regenerating", invite.getEmail());
                invite.setInvitationCode(generateInvitationCode());
            }
        }
        throw new IllegalStateException("Unable to generate a unique invitation code for " + invite.getEmail());
    }

    private String generateUniqueInvitationCode() {
        String code = generateInvitationCode();
        while (testInviteRepository.findByInvitationCode(code) != null) {
            code = generateInvitationCode();
        }
        return code;
    }

    private String generateInvitationCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return code.toString();
    }

    private long backoffSeconds(int attempt) {
        return Math.min(60L, 5L << (attempt - 1));
    }

    private void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
