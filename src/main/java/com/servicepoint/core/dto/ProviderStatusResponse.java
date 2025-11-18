package com.servicepoint.core.dto;


public class ProviderStatusResponse {
    private String status; // approved, pending, rejected, not_found
    private String message;
    private boolean canLogin;

    public ProviderStatusResponse(String status, String message, boolean canLogin) {
        this.status = status;
        this.message = message;
        this.canLogin = canLogin;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public boolean isCanLogin() { return canLogin; }
}
