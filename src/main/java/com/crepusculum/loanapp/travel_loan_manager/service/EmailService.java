package com.crepusculum.loanapp.travel_loan_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@crepusculum.com}")
    private String fromEmail;

    @Value("${app.password-reset.frontend-reset-url:http://localhost:5173/reset-password}")
    private String resetBaseUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = resetBaseUrl + "?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - Crepusculum Loan Manager");
        message.setText(buildEmailBody(resetLink));

        try {
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildEmailBody(String resetLink) {
        return "You have requested to reset your password.\n\n" +
               "Click the link below to reset your password:\n" +
               resetLink + "\n\n" +
               "This link will expire in 1 hour.\n\n" +
               "If you did not request this, please ignore this email.\n\n" +
               "- Crepusculum Loan Manager Team";
    }
}
