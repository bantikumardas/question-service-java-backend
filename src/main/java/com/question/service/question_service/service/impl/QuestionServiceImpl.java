package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.AddCodingQuestionRequest;
import com.question.service.question_service.dto.request.AddMcqRequest;
import com.question.service.question_service.dto.request.AddTestCaseRequest;
import com.question.service.question_service.dto.response.CodingQuestionResponse;
import com.question.service.question_service.dto.response.McqResponse;
import com.question.service.question_service.dto.response.TestCaseResponse;
import com.question.service.question_service.exception.ResourceNotFoundException;
import com.question.service.question_service.models.CodingQuestion;
import com.question.service.question_service.models.Test;
import com.question.service.question_service.models.TestCase;
import com.question.service.question_service.models.TestQuestion;
import com.question.service.question_service.repository.CodingQuestionRepository;
import com.question.service.question_service.repository.TestCaseRepository;
import com.question.service.question_service.repository.TestQuestionRepository;
import com.question.service.question_service.repository.TestRepository;
import com.question.service.question_service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final CodingQuestionRepository codingQuestionRepository;
    private final TestCaseRepository testCaseRepository;

    @Override
    @Transactional
    public McqResponse addMcq(AddMcqRequest request) {
        if(request.getCorrectOption()==null) {
            throw new ResourceNotFoundException("Correct option cannot be null or empty");
        }
        if(request.getQuestion()==null || request.getQuestion().isEmpty()) {
            throw new ResourceNotFoundException("Question cannot be null or empty");

        }
        Test test = findTestOrThrow(request.getTestId());
        int orderIndex = testQuestionRepository.countByTest_TestId(request.getTestId());

        TestQuestion cq=null;
        if(request.getQuestionId()!=null) {
            cq = testQuestionRepository.findById(request.getQuestionId()).get();
            orderIndex = cq.getOrderIndex();
        }

        TestQuestion question = TestQuestion.builder()
                .questionId(request.getQuestionId())
                .test(test)
                .question(request.getQuestion())
                .questionImageUrl(request.getQuestionImageUrl())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctOption(request.getCorrectOption())
                .marks(request.getMarks())
                .orderIndex(orderIndex)
                .level(request.getLevel())
                .build();

        TestQuestion saved = testQuestionRepository.save(question);
        log.info("Added MCQ questionId={} to testId={}", saved.getQuestionId(), request.getTestId());

        return McqResponse.from(saved);
    }

    @Override
    @Transactional
    public CodingQuestionResponse addCodingQuestion(AddCodingQuestionRequest request) {
        Test test = findTestOrThrow(request.getTestId());

        int orderIndex = codingQuestionRepository.countByTest_TestId(request.getTestId());
        CodingQuestion cq=null;
        if(request.getCodingQuestionId()!=null) {
            cq = codingQuestionRepository.findById(request.getCodingQuestionId()).get();
            orderIndex = cq.getOrderIndex();
        }

        CodingQuestion codingQuestion = CodingQuestion.builder()
                .codingQuestionId(request.getCodingQuestionId())
                .test(test)
                .title(request.getTitle())
                .description(request.getDescription())
                .paragraphs(request.getParagraphs())
                .constraints(request.getConstraints())
                .imageUrl1(request.getImageUrl1())
                .imageUrl2(request.getImageUrl2())
                .difficulty(request.getDifficulty())
                .marks(request.getMarks())
                .orderIndex(orderIndex)
                .build();

        CodingQuestion saved = codingQuestionRepository.save(codingQuestion);
        log.info("Added coding questionId={} to testId={}", saved.getCodingQuestionId(), request.getTestId());

        if (request.getCodingQuestionId() != null) {
            testCaseRepository.deleteByCodingQuestion_CodingQuestionId(saved.getCodingQuestionId());
        }

        for (AddTestCaseRequest testCaseRequest : request.getTestCases()) {
            TestCase testCase = TestCase.builder()
                    .codingQuestion(saved)
                    .input(testCaseRequest.getInput())
                    .expectedOutput(testCaseRequest.getExpectedOutput())
                    .explanation(testCaseRequest.getExplanation())
                    .isHidden(testCaseRequest.getIsHidden())
                    .isExample(testCaseRequest.getIsExample())
                    .build();
            testCaseRepository.save(testCase);
        }

        return CodingQuestionResponse.from(saved);
    }

    @Override
    @Transactional
    public TestCaseResponse addTestCase(AddTestCaseRequest request) {
        CodingQuestion codingQuestion = codingQuestionRepository.findById(request.getCodingQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coding question not found with id: " + request.getCodingQuestionId()));

        TestCase testCase = TestCase.builder()
                .codingQuestion(codingQuestion)
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .explanation(request.getExplanation())
                .isHidden(request.getIsHidden())
                .isExample(request.getIsExample())
                .build();

        TestCase saved = testCaseRepository.save(testCase);
        log.info("Added testCaseId={} to codingQuestionId={}", saved.getTestCaseId(), request.getCodingQuestionId());

        return TestCaseResponse.from(saved);
    }

    private Test findTestOrThrow(java.util.UUID testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));
    }
}
