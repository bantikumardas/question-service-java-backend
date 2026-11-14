package com.question.service.question_service.service.impl;

import java.util.UUID;

record BulkUploadRowResult(int rowNumber, boolean success, UUID questionId, String error) {

    static BulkUploadRowResult success(int rowNumber, UUID questionId) {
        return new BulkUploadRowResult(rowNumber, true, questionId, null);
    }

    static BulkUploadRowResult failure(int rowNumber, String error) {
        return new BulkUploadRowResult(rowNumber, false, null, error);
    }
}
