package com.question.service.question_service.service.impl;

import java.util.UUID;

record BulkMcqRow(
        int rowNumber,
        UUID questionId,
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctOption,
        String level,
        int marks,
        int orderIndex
) {
    BulkMcqRow withOrderIndex(int newOrderIndex) {
        return new BulkMcqRow(rowNumber, questionId, question, optionA, optionB, optionC, optionD,
                correctOption, level, marks, newOrderIndex);
    }
}
