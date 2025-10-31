package com.servicepoint.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Request to send OTP
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequest {

    @NotBlank(message = "Email is required")
    private String email;

    private String purpose; // "registration", "login", "password_reset"

    // Constructor for simple email-only requests
    public SendOtpRequest(String email) {
        this.email = email;
    }
}