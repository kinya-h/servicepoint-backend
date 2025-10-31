package com.servicepoint.core.service;

import com.servicepoint.core.model.OtpCode;
import com.servicepoint.core.repository.OtpRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;

    /**
     * Generate and send OTP to user's email
     */
    public void generateAndSendOtp(String email, String purpose) throws MessagingException {
        // Generate 6-digit OTP
        String otpCode = generateOtpCode();

        // Create OTP entity
        OtpCode otp = new OtpCode();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setPurpose(purpose);
        otp.setIsUsed(false);

        // Save to database
        otpRepository.save(otp);

        // Send email
        emailService.sendOtpEmail(email, otpCode, purpose);
    }

    /**
     * Verify OTP code
     */
    @Transactional
    public boolean verifyOtp(String email, String otpCode, String purpose) {
        Optional<OtpCode> otpOptional = otpRepository
                .findByEmailAndOtpCodeAndPurposeAndIsUsedFalse(email, otpCode, purpose);

        if (otpOptional.isEmpty()) {
            return false;
        }

        OtpCode otp = otpOptional.get();

        // Check if expired
        if (otp.isExpired()) {
            return false;
        }

        // Mark as used
        otp.setIsUsed(true);
        otp.setUsedAt(Timestamp.valueOf(LocalDateTime.now()));
        otpRepository.save(otp);

        return true;
    }

    /**
     * Check if OTP can be resent (rate limiting)
     */
    public boolean canResendOtp(String email, String purpose) {
        Optional<OtpCode> lastOtp = otpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose);

        if (lastOtp.isEmpty()) {
            return true;
        }

        // Allow resend after 1 minute
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        return lastOtp.get().getCreatedAt().toLocalDateTime().isBefore(oneMinuteAgo);
    }

    /**
     * Generate random 6-digit OTP
     */
    private String generateOtpCode() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Scheduled task to clean up expired OTPs (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredOtps() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        otpRepository.deleteByExpiresAtBefore(now);
    }
}