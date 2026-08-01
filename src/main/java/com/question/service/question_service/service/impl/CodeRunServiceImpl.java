package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CodeRunRequest;
import com.question.service.question_service.dto.response.CodeRunnerResponse;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.service.CodeRunService;
import com.question.service.question_service.service.CodeRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeRunServiceImpl implements CodeRunService {

    @Autowired
    private CodeRunner codeRunner;

    @Override
    public CodeRunnerResponse run(CodeRunRequest request) {
        CodeRunRequest.Language language=request.getLanguage();
        String code=request.getCode();
        List<String> input=request.getInput();
        if(input==null||input.isEmpty()){
            throw new BadRequestException("Code is Empty");
        }
        return  null;

    }
}
