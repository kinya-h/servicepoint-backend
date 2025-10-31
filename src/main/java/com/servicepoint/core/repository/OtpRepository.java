package com.servicepoint.core.repository;

import com.servicepoint.core.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Integer> {

    Optional<OtpCode> findByEmailAndOtpCodeAndPurposeAndIsUsedFalse(
            String email,
            String otpCode,
            String purpose
    );

    Optional<OtpCode> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email,
            String purpose
    );

    // Clean up expired OTPs
    void deleteByExpiresAtBefore(Timestamp timestamp);
}