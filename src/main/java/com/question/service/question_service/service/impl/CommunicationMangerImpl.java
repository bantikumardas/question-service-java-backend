package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CommunicatonRequest;
import com.question.service.question_service.dto.response.CommunicationResponse;
import com.question.service.question_service.service.CommunicationManger;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Synchronous by design - callers (e.g. TestInviteDispatcher) own the async dispatch
 * and need a real boolean result back, which @Async on a non-Future return type can't give:
 * Spring's async proxy would fire this on a background thread and return null immediately.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationMangerImpl implements CommunicationManger {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final JavaMailSender mailSender;

    @Value("${invite.mail-from}")
    private String mailFrom;

    @Override
    public boolean emailCommunication(CommunicatonRequest request) {

        String email=request.getEmailOrPhoneNumber();
        String subject=request.getSubject();
        String body=request.getMessage();
        log.info("[{}] Sending email to: {}", Thread.currentThread().getName(), email);
        if(subject==null || subject.isEmpty()){
            throw new ValidationException("subject is empty");
        }
        if(!isValidEmail(email)){
            throw  new ValidationException("email is not valid");
        }
        System.out.println("Message for checking this function is run or not : "+body);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Mail accepted by SMTP server. From: {}, To: {}, Subject: {}, Message-ID: {}",
                    mailFrom, email, subject, message.getMessageID());
            return true;

        } catch (MailAuthenticationException e) {
            // Wrong username/password/app-password
            log.error("Mail auth failed sending to {}: {}", email, e.getMessage(), e);
            return false;

        } catch (MailSendException e) {
            // Invalid email, recipient rejected, connection refused
            log.error("Mail send failed sending to {}: {}", email, e.getMessage(), e);
            return false;

        } catch (MailException e) {
            // Any other mail error (parent of all Spring mail exceptions)
            log.error("Mail error sending to {}: {}", email, e.getMessage(), e);
            return false;

        } catch (Exception e) {
            // MimeMessageHelper errors (encoding, attachment issues)
            log.error("Unexpected error sending to {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public CommunicationResponse smsCommunication(CommunicatonRequest request) {
        return null;
    }

    @Override
    public CommunicationResponse whatsappCommunication(CommunicatonRequest request) {
        return null;
    }
}
