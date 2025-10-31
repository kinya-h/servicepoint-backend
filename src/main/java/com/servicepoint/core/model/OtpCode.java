package com.servicepoint.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp_codes")
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer otpId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private String purpose; // "registration", "login", "password_reset"

    @Column(nullable = false)
    private Boolean isUsed = false;

    @Column(nullable = false)
    private Timestamp expiresAt;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    private Timestamp usedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        // OTP expires in 10 minutes
        expiresAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));
    }

    public boolean isExpired() {
        return Timestamp.valueOf(LocalDateTime.now()).after(expiresAt);
    }
}