package com.question.service.question_service.repository;

import com.question.service.question_service.models.TestInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TestInviteRepository extends JpaRepository<TestInvite, UUID> {

    List<TestInvite> findByTest_TestIdAndEmailIn(UUID testId, Set<String> emails);

    List<TestInvite> findByTest_TestId(UUID testId);

    Optional<TestInvite> findByInviteIdAndTest_TestId(UUID inviteId, UUID testId);

    List<TestInvite> findByStatusAndAttemptCountLessThan(TestInvite.Status status, int attemptCount);

    TestInvite findByInvitationCode(String invitationCode);

}
