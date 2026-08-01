package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.StartExamRequest;
import com.question.service.question_service.dto.response.StartExamResponse;

public interface ExamService {

    StartExamResponse startExam(StartExamRequest request);
}
