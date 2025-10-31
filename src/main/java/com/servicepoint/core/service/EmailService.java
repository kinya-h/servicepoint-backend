package com.servicepoint.core.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otpCode, String purpose) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(getSubjectByPurpose(purpose));

        String htmlContent = buildHtmlContent(otpCode, purpose);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String getSubjectByPurpose(String purpose) {
        return switch (purpose) {
            case "registration" -> "Complete Your Registration - OTP Code";
            case "login" -> "Your Login Verification Code";
            case "password_reset" -> "Password Reset Verification Code";
            default -> "Your Verification Code";
        };
    }

    private String buildHtmlContent(String otpCode, String purpose) {
        String actionText = switch (purpose) {
            case "registration" -> "complete your registration";
            case "login" -> "log in to your account";
            case "password_reset" -> "reset your password";
            default -> "verify your identity";
        };

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; margin-top: 20px; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #4CAF50; text-align: center; 
                                letter-spacing: 5px; padding: 20px; background-color: white; 
                                border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }
                    .warning { color: #d32f2f; font-size: 14px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>ServicePoint Verification</h1>
                    </div>
                    <div class="content">
                        <h2>Hello!</h2>
                        <p>You requested to %s. Please use the following One-Time Password (OTP):</p>
                        <div class="otp-code">%s</div>
                        <p><strong>This code will expire in 10 minutes.</strong></p>
                        <p>If you didn't request this code, please ignore this email or contact support if you have concerns.</p>
                        <div class="warning">
                            ⚠️ Never share this code with anyone. ServicePoint will never ask for your OTP.
                        </div>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 ServicePoint. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(actionText, otpCode);
    }
}