package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CreateTestRequest;
import com.question.service.question_service.dto.response.*;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.exception.DuplicateResourceException;
import com.question.service.question_service.exception.ResourceNotFoundException;
import com.question.service.question_service.exception.UnauthorizedException;
import com.question.service.question_service.models.*;
import com.question.service.question_service.repository.TestRepository;
import com.question.service.question_service.repository.UserRepository;
import com.question.service.question_service.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_INVITES_PER_REQUEST = 50;

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final TestInviteDispatcher testInviteDispatcher;


    @Override
    @Transactional
    public TestResponse createTest(CreateTestRequest request) {
        if (testRepository.existsByTestName(request.getTestName())) {
            throw new DuplicateResourceException("Test with name '" + request.getTestName() + "' already exists");
        }
        User user = getCurrentUser();
        Boolean isAdmin = user.getRole() == User.Role.ADMIN;
        Boolean isCaAdmin = user.getRole() == User.Role.CAADMIN;
        Company company = user.getCompany();
        Test test = Test.builder()
                .testName(request.getTestName())
                .totalTimeSeconds((long) request.getTotalTimeMinute() * 60)
                .createdBy(user.getUserId())
                .isCreatedByCAAdmin(isCaAdmin)
                .isCreatedByAdmin(isAdmin)
                .company(company)
                .build();

        Test saved = testRepository.save(test);
        log.info("Created test with id={} name='{}' by createdBy={}", saved.getTestId(), saved.getTestName(), saved.getCreatedBy());

        return TestResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestResponse> getAllTest(int page, int size, String sortBy, String status, String sortDir, String query) {
        User user = getCurrentUser();
        if(user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.CAADMIN) {
            throw new UnauthorizedException("You are not authorized to perform this action");
        }
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Test> testPage;
        if (query != null && !query.isEmpty()) {
            if(user.getRole() == User.Role.ADMIN) {
                testPage = testRepository.findByTestNameContainingIgnoreCase(query, pageable);
            } else if(user.getRole() == User.Role.CAADMIN) {
                testPage = testRepository.findByTestNameContainingIgnoreCaseAndCompany_CompanyId(query, user.getCompany().getCompanyId(), pageable);
            } else {
                throw new UnauthorizedException("You are not authorized to perform this action");
            }
        } else {
            if(user.getRole() == User.Role.ADMIN) {
                testPage = testRepository.findAll(pageable);
            } else if(user.getRole() == User.Role.CAADMIN) {
                testPage = testRepository.findByCompany_CompanyId(user.getCompany().getCompanyId(), pageable);
            } else {
                throw new UnauthorizedException("You are not authorized to perform this action");
            }
        }
        Page<TestResponse> responsePage = testPage.map(test ->
                TestResponse.builder()
                        .testId(test.getTestId())
                        .testName(test.getTestName())
                        .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                        .createdBy(test.getCreatedBy())
                        .createdTime(test.getCreatedTime())
                        .status(test.getStatus().name())
                        .totalQuestions(test.getCodingQuestions().size() + test.getTestQuestions().size())
                        .totalCodingQuestions(test.getCodingQuestions().size())
                        .build()
        );
        return responsePage;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTestResponse getAllQuestionFromTest(String testId) {
        Test test = testRepository.findById(UUID.fromString(testId))
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));

        List<McqResponse> mcqResponses = new ArrayList<>();
        if (test.getTestQuestions() != null) {
            for (TestQuestion tq : test.getTestQuestions()) {
                String question = tq.getQuestion();
                mcqResponses.add(McqResponse.builder()
                        .questionId(tq.getQuestionId())
                        .testId(tq.getTest().getTestId())
                        .question(question)
                        .questionImageUrl(tq.getQuestionImageUrl())
                        .optionA(tq.getOptionA())
                        .optionB(tq.getOptionB())
                        .optionC(tq.getOptionC())
                        .optionD(tq.getOptionD())
                        .correctOption(tq.getCorrectOption().name())
                        .marks(tq.getMarks())
                        .orderIndex(tq.getOrderIndex())
                        .level(tq.getLevel().name())
                        .build());
            }
        }

        List<CodingQuestionResponse> codingQuestionResponses = new ArrayList<>();
        if (test.getCodingQuestions() != null) {
            for (CodingQuestion cq : test.getCodingQuestions()) {
                codingQuestionResponses.add(CodingQuestionResponse.builder()
                        .codingQuestionId(cq.getCodingQuestionId())
                        .testId(cq.getTest().getTestId())
                        .title(cq.getTitle())
                        .description(cq.getDescription())
                        .paragraphs(new ArrayList<>(cq.getParagraphs()))
                        .constraints(new ArrayList<>(cq.getConstraints()))
                        .imageUrl1(cq.getImageUrl1())
                        .imageUrl2(cq.getImageUrl2())
                        .difficulty(cq.getDifficulty().name())
                        .marks(cq.getMarks())
                        .orderIndex(cq.getOrderIndex())
                        .build());
            }
        }

        return AdminTestResponse.builder()
                .testId(test.getTestId())
                .testName(test.getTestName())
                .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                .createdBy(test.getCreatedBy())
                .createdTime(test.getCreatedTime())
                .status(test.getStatus().name())
                .totalQuestions(mcqResponses.size() + codingQuestionResponses.size())
                .totalCodingQuestions(codingQuestionResponses.size())
                .mcqQuestions(mcqResponses)
                .codingQuestionResponses(codingQuestionResponses)
                .build();
    }


    @Override
    public void changeTestStatus(UUID testId, String targetStatus) {
        if (!testRepository.existsById(testId)) {
            throw new ResourceNotFoundException("Test not found with id: " + testId);
        }
        if (targetStatus == null || targetStatus.isEmpty()) {
            throw new BadRequestException("targetStatus must not be empty");
        }
        Test test = testRepository.findById(testId).get();
        if (test.getStatus().name().equals(targetStatus)) {
            throw new BadRequestException("Already in target status");
        }
        if (targetStatus.equals("DRAFT")) {
            test.setStatus(Test.Status.DRAFT);
        } else if (targetStatus.equals("ACTIVE")) {
            test.setStatus(Test.Status.ACTIVE);
        } else if (targetStatus.equals("ARCHIVED")) {
            test.setStatus(Test.Status.ARCHIVED);
        } else {
            throw new BadRequestException("Target Status is incorrect");
        }
        testRepository.save(test);
    }

    @Override
    @Transactional(readOnly = true)
    public String sendTestInvite(UUID testId, String emails) {
        User user = getCurrentUser();
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));

        if (test.getStatus() != Test.Status.ACTIVE) {
            throw new BadRequestException("Test Status should be active");
        }
        if (user.getRole() != User.Role.ADMIN && !test.getCreatedBy().equals(user.getUserId())) {
            throw new UnauthorizedException("You are not authorized to send invites for this test");
        }

        Set<String> emailSet = parseAndValidateEmails(emails);

        // Force-initialize the lazy collections the invite email template reads, since
        // dispatch() continues on a different thread after this transaction/session closes.
        test.getCodingQuestions().size();
        test.getTestQuestions().size();
        System.out.println("Calling send email job");
        testInviteDispatcher.dispatch(user, test, emailSet);

        return "Sending email done";
    }

    private Set<String> parseAndValidateEmails(String emails) {
        if (emails == null || emails.isBlank()) {
            throw new BadRequestException("emails must not be empty");
        }

        Set<String> emailSet = Arrays.stream(emails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (emailSet.isEmpty()) {
            throw new BadRequestException("emails must not be empty");
        }
        if (emailSet.size() > MAX_INVITES_PER_REQUEST) {
            throw new BadRequestException("Cannot invite more than " + MAX_INVITES_PER_REQUEST + " emails in a single request");
        }

        List<String> invalid = emailSet.stream()
                .filter(email -> !EMAIL_PATTERN.matcher(email).matches())
                .toList();
        if (!invalid.isEmpty()) {
            throw new BadRequestException("Invalid email address(es): " + String.join(", ", invalid));
        }

        return emailSet;
    }

    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName == null || userName.isEmpty()) {
            throw new BadRequestException("You are not authorized to perform this action");
        }
        User user = userRepository.findByUserId(UUID.fromString(userName))
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userName));
        return user;
    }

}
