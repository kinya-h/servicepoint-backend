package com.servicepoint.core.dto;

public record RegisterWithOtpRequest(
        String username,
        String email,
        String password,
        String role,
        String location,
        Double latitude,
        Double longitude,
        String otpCode // OTP code for verification
) {}