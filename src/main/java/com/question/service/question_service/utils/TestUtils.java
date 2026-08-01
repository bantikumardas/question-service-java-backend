package com.question.service.question_service.utils;

import com.question.service.question_service.models.Test;
import com.question.service.question_service.models.TestInvite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestUtils {

    @Value("${frontend.baseurl}")
    private String FRONTEND_BASE_URL;

    public String getInvitationHtmlCode(Test test, TestInvite testInvite) {
        String htmlCode = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Exam Invitation</title>\n" +
                "</head>\n" +
                "<body style=\"margin:0; padding:0; background-color:#f0f2f5; font-family: Arial, Helvetica, sans-serif;\">\n" +
                "\n" +
                "    <!-- Outer container -->\n" +
                "    <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f0f2f5; padding:30px 0;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\">\n" +
                "\n" +
                "                <!-- Inner card -->\n" +
                "                <table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 15px rgba(0,0,0,0.08);\">\n" +
                "\n" +
                "                    <!-- Header -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"background: linear-gradient(135deg, #1a73e8, #0d47a1); padding:40px 40px 30px; text-align:center;\">\n" +
                "                            <div style=\"width:60px; height:60px; background-color:rgba(255,255,255,0.2); border-radius:50%; margin:0 auto 16px; line-height:60px; font-size:28px;\">\n" +
                "                                \uD83D\uDCDD\n" +
                "                            </div>\n" +
                "                            <h1 style=\"margin:0; color:#ffffff; font-size:24px; font-weight:700; letter-spacing:0.5px;\">\n" +
                "                                You're Invited to an Exam\n" +
                "                            </h1>\n" +
                "                            <p style=\"margin:8px 0 0; color:rgba(255,255,255,0.85); font-size:14px;\">\n" +
                "                                Online Assessment Platform\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Greeting -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:30px 40px 10px;\">\n" +
                "                            <p style=\"margin:0; font-size:16px; color:#333333;\">\n" +
                "                                Hi candidate,\n" +
                "                            </p>\n" +
                "                            <p style=\"margin:12px 0 0; font-size:14px; color:#555555; line-height:22px;\">\n" +
                "                                You have been invited to take an online exam. Please review the details below and click the button to start your test.\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Exam Details Card -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:20px 40px;\">\n" +
                "                            <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f8f9fb; border-radius:10px; border:1px solid #e8eaed;\">\n" +
                "\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:20px 24px 12px;\">\n" +
                "                                        <p style=\"margin:0; font-size:11px; text-transform:uppercase; letter-spacing:1px; color:#1a73e8; font-weight:700;\">\n" +
                "                                            Exam Details\n" +
                "                                        </p>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                                <!-- Exam Name -->\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 24px;\">\n" +
                "                                        <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td style=\"padding:10px 0; border-bottom:1px solid #e8eaed;\">\n" +
                "                                                    <span style=\"font-size:13px; color:#888888;\">Exam Name</span><br>\n" +
                "                                                    <span style=\"font-size:15px; color:#222222; font-weight:600;\">{{examName}}</span>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                                <!-- Date & Time -->\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 24px;\">\n" +
                "                                        <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"50%\" style=\"padding:10px 0; border-bottom:1px solid #e8eaed;\">\n" +
                "                                                    <span style=\"font-size:13px; color:#888888;\">Exam window</span><br>\n" +
                "                                                    <span style=\"font-size:15px; color:#222222; font-weight:600;\">{{examWindow}}</span>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                                <!-- Duration & Questions -->\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 24px;\">\n" +
                "                                        <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                            <tr>\n" +
                "                                                <td width=\"50%\" style=\"padding:10px 0; border-bottom:1px solid #e8eaed;\">\n" +
                "                                                    <span style=\"font-size:13px; color:#888888;\">Duration</span><br>\n" +
                "                                                    <span style=\"font-size:15px; color:#222222; font-weight:600;\">{{duration}}</span>\n" +
                "                                                </td>\n" +
                "                                                <td width=\"50%\" style=\"padding:10px 0; border-bottom:1px solid #e8eaed;\">\n" +
                "                                                    <span style=\"font-size:13px; color:#888888;\">Total Questions</span><br>\n" +
                "                                                    <span style=\"font-size:15px; color:#222222; font-weight:600;\">{{totalQuestions}}</span>\n" +
                "                                                </td>\n" +
                "                                            </tr>\n" +
                "                                        </table>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Access Code -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:0 40px 20px; text-align:center;\">\n" +
                "                            <p style=\"margin:0 0 8px; font-size:13px; color:#888888;\">Your Access Code</p>\n" +
                "                            <div style=\"display:inline-block; background-color:#fff3e0; border:2px dashed #ff9800; border-radius:8px; padding:12px 28px;\">\n" +
                "                                <span style=\"font-size:22px; font-weight:700; color:#e65100; letter-spacing:4px; font-family:monospace;\">\n" +
                "                                    {{accessCode}}\n" +
                "                                </span>\n" +
                "                            </div>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- CTA Button -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:10px 40px 30px; text-align:center;\">\n" +
                "                            <a href=\"{{examLink}}\"\n" +
                "                               style=\"display:inline-block; background-color:#1a73e8; color:#ffffff; text-decoration:none; padding:14px 40px; border-radius:8px; font-size:16px; font-weight:600; letter-spacing:0.3px;\">\n" +
                "                                Start Exam →\n" +
                "                            </a>\n" +
                "                            <p style=\"margin:14px 0 0; font-size:12px; color:#999999;\">\n" +
                "                                Or copy this link: <a href=\"{{examLink}}\" style=\"color:#1a73e8; word-break:break-all;\">{{examLink}}</a>\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Divider -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:0 40px;\">\n" +
                "                            <hr style=\"border:none; border-top:1px solid #e8eaed; margin:0;\">\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Instructions -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:24px 40px;\">\n" +
                "                            <p style=\"margin:0 0 12px; font-size:14px; font-weight:700; color:#333333;\">\n" +
                "                                \uD83D\uDCCB Instructions\n" +
                "                            </p>\n" +
                "                            <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:4px 0; font-size:13px; color:#555555; line-height:20px;\">\n" +
                "                                        <span style=\"color:#1a73e8; font-weight:700; margin-right:8px;\">1.</span>\n" +
                "                                        Ensure a stable internet connection throughout the exam.\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:4px 0; font-size:13px; color:#555555; line-height:20px;\">\n" +
                "                                        <span style=\"color:#1a73e8; font-weight:700; margin-right:8px;\">2.</span>\n" +
                "                                        Use a laptop or desktop with a webcam (if proctored).\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:4px 0; font-size:13px; color:#555555; line-height:20px;\">\n" +
                "                                        <span style=\"color:#1a73e8; font-weight:700; margin-right:8px;\">3.</span>\n" +
                "                                        Do not switch tabs or open other applications during the exam.\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:4px 0; font-size:13px; color:#555555; line-height:20px;\">\n" +
                "                                        <span style=\"color:#1a73e8; font-weight:700; margin-right:8px;\">4.</span>\n" +
                "                                        Submit your answers before the timer runs out.\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:4px 0; font-size:13px; color:#555555; line-height:20px;\">\n" +
                "                                        <span style=\"color:#1a73e8; font-weight:700; margin-right:8px;\">5.</span>\n" +
                "                                        Contact support if you face any technical issues.\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Deadline Warning -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding:0 40px 24px;\">\n" +
                "                            <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#fff8e1; border-left:4px solid #ffc107; border-radius:4px;\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:14px 16px;\">\n" +
                "                                        <p style=\"margin:0; font-size:13px; color:#795548;\">\n" +
                "                                            ⚠\uFE0F <strong>Deadline:</strong> This invitation expires on <strong>{{expiryDate}}</strong>. Please complete your exam before the deadline.\n" +
                "                                        </p>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                    <!-- Footer -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"background-color:#f8f9fb; padding:24px 40px; text-align:center; border-top:1px solid #e8eaed;\">\n" +
                "                            <p style=\"margin:0 0 6px; font-size:13px; color:#666666;\">\n" +
                "                                Need help? Contact us at\n" +
                "                                <a href=\"mailto:{{supportEmail}}\" style=\"color:#1a73e8; text-decoration:none;\">{{supportEmail}}</a>\n" +
                "                            </p>\n" +
                "                            <p style=\"margin:0; font-size:11px; color:#aaaaaa;\">\n" +
                "                                © 2026 Question Service. All rights reserved.\n" +
                "                            </p>\n" +
                "                            <p style=\"margin:8px 0 0; font-size:11px; color:#aaaaaa;\">\n" +
                "                                This is an automated email. Please do not reply.\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "\n" +
                "                </table>\n" +
                "                <!-- /Inner card -->\n" +
                "\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <!-- /Outer container -->\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        String examLink=FRONTEND_BASE_URL+"/"+testInvite.getInvitationCode();
        String html = htmlCode
                .replace("{{examName}}", test.getTestName())
                .replace("{{examWindow}}", "Please attempt within 24 hours")
                .replace("{{duration}}", (test.getTotalTimeSeconds()/60)+" Minutes")
                .replace("{{totalQuestions}}", (test.getCodingQuestions().size()+test.getTestQuestions().size())+"")
                .replace("{{accessCode}}", testInvite.getInvitationCode())
                .replace("{{examLink}}", examLink)
                .replace("{{expiryDate}}", "24 hours")
                .replace("{{supportEmail}}", "support@question-service.com");
        return html;
    }
}
