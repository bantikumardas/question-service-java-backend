package com.question.service.question_service.service;

import com.question.service.question_service.dto.request.CommunicatonRequest;
import com.question.service.question_service.dto.response.CommunicationResponse;

public interface CommunicationManger {
    boolean emailCommunication(CommunicatonRequest request);
    CommunicationResponse smsCommunication(CommunicatonRequest request);
    CommunicationResponse whatsappCommunication(CommunicatonRequest request);

}
