package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.AddCodingQuestionRequest;
import com.question.service.question_service.dto.request.AddMcqRequest;
import com.question.service.question_service.dto.request.AddTestCaseRequest;
import com.question.service.question_service.dto.response.CodingQuestionResponse;
import com.question.service.question_service.dto.response.McqResponse;
import com.question.service.question_service.dto.response.TestCaseResponse;

public interface QuestionService {

    McqResponse addMcq(AddMcqRequest request);

    CodingQuestionResponse addCodingQuestion(AddCodingQuestionRequest request);

    TestCaseResponse addTestCase(AddTestCaseRequest request);
}
