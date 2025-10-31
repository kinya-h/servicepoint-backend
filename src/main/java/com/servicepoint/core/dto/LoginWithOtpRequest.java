package com.servicepoint.core.dto;

public record LoginWithOtpRequest(
        String username,
        String password,
        String otpCode // Optional, can be null for first step
) {}
