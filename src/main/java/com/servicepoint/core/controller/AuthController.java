package com.servicepoint.core.controller;

import com.servicepoint.core.dto.*;
import com.servicepoint.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Step 1: Request OTP for registration
     * POST /api/auth/register/request-otp
     */
    @PostMapping("/register/request-otp")
    public ResponseEntity<?> requestRegistrationOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            SendOtpResponse response = userService.initiateRegistration(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("OTP request failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("OTP request failed", "An unexpected error occurred"));
        }
    }

    /**
     * Step 2: Complete registration with OTP
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest user, HttpServletRequest request) {
        try {
            UserResponse userDTO = userService.createUser(user, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed", "An unexpected error occurred"));
        }
    }

    /**
     * Step 1: Request OTP for login
     * POST /api/auth/login/request-otp
     */
    @PostMapping("/login/request-otp")
    public ResponseEntity<?> requestLoginOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            SendOtpResponse response = userService.initiateLogin(request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("OTP request failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("OTP request failed", "An unexpected error occurred"));
        }
    }

    /**
     * Step 2: Complete login with credentials and OTP
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest httpRequest) {
        try {
            LoginResponse response = userService.loginUser(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Login failed", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed", "An unexpected error occurred"));
        }
    }

    // Inner class for error responses
    public static class ErrorResponse {
        private String error;
        private String message;
        private long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}