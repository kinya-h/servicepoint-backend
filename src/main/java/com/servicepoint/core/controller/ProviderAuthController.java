package com.servicepoint.core.controller;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.service.ProviderAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider-auth")
@CrossOrigin(origins = "*")
public class ProviderAuthController {

    @Autowired
    private ProviderAuthService providerAuthService;

    /**
     * Check provider registration status
     * GET /api/provider-auth/status?email=provider@example.com
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam String email) {
        try {
            var status = providerAuthService.checkProviderStatus(email);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Status check failed", e.getMessage()));
        }
    }

    /**
     * Provider login - checks approval status then logs in
     * POST /api/provider-auth/login
     *
     * Note: Providers can also use the regular /api/auth/login endpoint
     * This endpoint adds an extra validation layer for provider status
     */
    @PostMapping("/login")
    public ResponseEntity<?> providerLogin(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        try {
            LoginResponse response = providerAuthService.providerLogin(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Provider login failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Provider login failed", "An unexpected error occurred"));
        }
    }

    // Error response class
    static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}